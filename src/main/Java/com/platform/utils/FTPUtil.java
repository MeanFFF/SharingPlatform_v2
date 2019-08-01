package com.platform.utils;


import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {

    private static  final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("开始连接ftp服务器,结束上传,上传结果:{}", result);
        return result;
    }


    // 批量上传
    // remotePath:远程路径,想上传到ftp服务器下(也是一个文件夹)的那个文件夹
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                // 切换文件夹
                ftpClient.changeWorkingDirectory(remotePath);
                // 设置缓冲区大小
                ftpClient.setBufferSize(1024);
                // 设置字节编码
                ftpClient.setControlEncoding("UTF-8");
                // 设置文件类型
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                // 打开本地的被动模式
                ftpClient.enterLocalPassiveMode();
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }

            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
                e.printStackTrace();
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    /**
     * 看下面downloadFile
     */
    public static boolean downloadFile(String filename, String fileAddress, String localpath) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.downloadFile("",filename, fileAddress,localpath);
        logger.info("开始连接ftp服务器,结束下载,下载结果:{}", result);
        return result;
    }


    /**
     *
     * @param pathname      ftp文件目录
     * @param filename      文件真实名
     * @param fileAddress   文件在ftp下的名字(uuid生成的)
     * @param localpath     本地存储路径(文件夹)
     * @return
     * @throws IOException
     */
    private boolean downloadFile(String pathname, String filename, String fileAddress, String localpath) throws IOException {
        boolean downloaded = true;
        FileOutputStream fos = null;

        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                logger.info("开始下载文件");
                // 更换目录
                ftpClient.changeWorkingDirectory(pathname);
                filename += fileAddress.substring(fileAddress.lastIndexOf("."));
                // 创建本地文件
                File localFile = new File(localpath + filename);
                fos = new FileOutputStream(localFile);
                // 将ftp中的文件输出到本地文件
                ftpClient.retrieveFile(fileAddress, fos);

            } catch (IOException e) {
                logger.error("下载文件异常",e);
                downloaded = false;
            }finally {
                fos.close();
                ftpClient.disconnect();
            }
        }
        return downloaded;
    }



    private boolean connectServer(String ip,int port,String user,String pwd){

        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }











    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}

