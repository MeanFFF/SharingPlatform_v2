package com.platform.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.platform.common.Const;
import com.platform.common.ServerResponse;
import com.platform.dao.OrderMapper;
import com.platform.dao.PayInfoMapper;
import com.platform.dao.ProductMapper;
import com.platform.dao.UserMapper;
import com.platform.pojo.Order;
import com.platform.pojo.PayInfo;
import com.platform.pojo.Product;
import com.platform.pojo.User;
import com.platform.redisService.IRedisOrderService;
import com.platform.redisService.IRedisProductService;
import com.platform.redisService.IRedisUserService;
import com.platform.service.IOrderService;
import com.platform.service.IProductService;
import com.platform.utils.DateTimeUtil;
import com.platform.utils.FTPUtil;
import com.platform.utils.PropertiesUtil;
import com.platform.vo.OrderVo;
import com.platform.vo.ProductVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


@Service
public class OrderServiceImpl implements IOrderService {

    private static AlipayTradeService tradeService;

    static {

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private IRedisOrderService iRedisOrderService;
    @Autowired
    private IRedisProductService iRedisProductService;
    @Autowired
    private IRedisUserService iRedisUserService;

    /**
     * 管理员获取订单列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getManageList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectList();
        List<OrderVo> orderListVo = assembleOrderVoList(orderList);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderListVo);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 获取订单详情页
     *
     * @param orderNo
     * @return
     */
    public ServerResponse<OrderVo> getOrderDetail(Long orderNo) {
        if (orderNo == null) {
            return ServerResponse.createByErrorMessage("订单号为空");
        }
        Order order = iRedisOrderService.getOrder(orderNo, null);
        if(order == null){
            return ServerResponse.createByErrorMessage("没有此订单");
        }


        Product product = iRedisProductService.getProduct(order.getProductId());
        if(product == null){
            return ServerResponse.createByErrorMessage("没有该商品");
        }
        ProductVo productVo = iProductService.assembleProductVo(product);
        OrderVo orderVo = assembleOrderVo(order, productVo);
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse createOrder(Integer userId, Integer productId) {
        // 获取商品金额

        Product product = iRedisProductService.getProduct(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("没有该商品");
        }

        BigDecimal payment = product.getPrice();
        Order order = this.assembleOrder(userId, productId, payment);
        if (order == null) {
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        Map map = Maps.newHashMap();
        map.put("orderNo", order.getOrderNo());
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse cancelOrder(Integer userId, Long orderNo) {
        Order order = iRedisOrderService.getOrder(orderNo, userId);
        if(order == null){
            return ServerResponse.createByErrorMessage("该用户此订单不存在");
        }

        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("已付款，无法取消订单");
        }
        order.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int rowCount = orderMapper.updateByPrimaryKeySelective(order);
        if (rowCount > 0) {
            // 更新缓存
            iRedisOrderService.saveOrUpdateOrderCache(order);
            return ServerResponse.createBySuccess("取消订单成功");
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse getOrderList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("create_time desc");
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(orderList);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    // todo
    public ServerResponse<OrderVo> userOrderDetail(Integer userId, Long orderNo) {
        Order order = iRedisOrderService.getOrder(orderNo, userId);
        if(order != null){
            return getOrderDetail(orderNo);
        }
        return ServerResponse.createByErrorMessage("没有找到该订单");
    }

    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = iRedisOrderService.getOrder(orderNo, userId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));


        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();


        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("Mean共享数据平台扫码支付,订单号:").append(outTradeNo).toString();


        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();


        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";


        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();


        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");


        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<>();

        Product product = iRedisProductService.getProduct(order.getProductId());
        GoodsDetail goods = GoodsDetail.newInstance(product.getId().toString(), product.getName(),
                product.getPrice().longValue(), 1);
        goodsDetailList.add(goods);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);


        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                //细节细节细节
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码异常", e);
                }
                logger.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }

    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }


    public ServerResponse aliCallback(Map<String, String> params) {
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        Order order = iRedisOrderService.getOrder(orderNo, null);
        if (order == null) {
            return ServerResponse.createByErrorMessage("非Mean共享数据平台的订单,回调忽略");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess("支付宝重复调用");
        }
        // 支付成功, 订单状态设为已支付, 用户增加积分
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            // 更新订单状态
            order.setFinishTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
            // 更新缓存
            iRedisOrderService.saveOrUpdateOrderCache(order);
            // 根据商品的积分数, 去更新用户的积分数
            Product product = iRedisProductService.getProduct(order.getProductId());
            String str = product.getName();
            if(product.getCategory() == Const.ProductCategoryEnum.SCORE.getCode()){
                int score = Integer.parseInt(str.substring(0, str.indexOf("积分")));
//                User user = userMapper.selectByPrimaryKey(order.getUserId());
                User user = iRedisUserService.getUser(order.getUserId());
                user.setTotalScore(user.getTotalScore() + score);
                userMapper.updateUserByPrimaryKeySelective(user);
                iRedisUserService.updateUserInfoCache(user.getId());
            }
        }

        // 添加支付信息, 会添加两次, 第一次(插入):状态为等待支付, 第二次(更新):状态为支付成功
        PayInfo payInfo =  payInfoMapper.selectByOrderNo(order.getOrderNo());
        if(payInfo == null){
            payInfo = new PayInfo();
            payInfo.setUserId(order.getUserId());
            payInfo.setOrderNo(order.getOrderNo());
            payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
            payInfo.setPayNumber(tradeNo);
            payInfo.setPayStatus(tradeStatus);
            payInfo.setCreateTime(new Date());
            payInfo.setUpdateTime(payInfo.getCreateTime());

            payInfoMapper.insert(payInfo);
        }else{
            payInfo.setPayStatus(tradeStatus);
            payInfo.setUpdateTime(new Date());
            payInfoMapper.updatePayInfoByPrimaryKeySelective(payInfo);
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 前台轮询订单状态
     * 支付宝支付成功, 回调, 改变订单状态
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = iRedisOrderService.getOrder(orderNo, userId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    private Order assembleOrder(Integer userId, Integer productId, BigDecimal payment) {
        Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        // 设置订单状态：未支付
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setUserId(userId);
        order.setProductId(productId);
        order.setPayment(payment);
        // 设置支付类型：在线支付
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setCreateTime(new Date());
        order.setUpdateTime(order.getCreateTime());
        int rowCount = orderMapper.insert(order);
        if (rowCount > 0) {
            return order;
        }
        return null;
    }


    /**
     * 按照时间戳和随机获得订单号
     *
     * @return
     */
    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }


    /**
     * 包装OrderVoList
     *
     * @param orderList
     * @return
     */
    private List<OrderVo> assembleOrderVoList(List<Order> orderList) {
        List<OrderVo> orderListVoList = Lists.newArrayList();
        for (Order order : orderList) {
//            Product product = productMapper.selectById(order.getProductId());
            Product product = iRedisProductService.getProduct(order.getProductId());
            ProductVo productVo = iProductService.assembleProductVo(product);
            OrderVo orderVo = assembleOrderVo(order, productVo);
            orderListVoList.add(orderVo);
        }
        return orderListVoList;
    }

    /**
     * 包装Order
     *
     * @param order
     * @param productVo
     * @return
     */
    private OrderVo assembleOrderVo(Order order, ProductVo productVo) {
        OrderVo orderVo = new OrderVo();
        orderVo.setUserId(order.getUserId());
        orderVo.setUsername(userMapper.selectByPrimaryKey(order.getUserId()).getUsername());
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setCreateTime(order.getCreateTime());
        orderVo.setProductVo(productVo);
        return orderVo;
    }

}
