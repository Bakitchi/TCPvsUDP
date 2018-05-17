package UDP.client;

import UDP.utils.UDPUtils;

import java.io.*;
import java.net.*;

/**
 * @Title: UDPClient.java
 * @Package UDP.client
 * @Description:
 * @Author: Bakitchi
 * @Date: 2018/5/12下午9:59
 */
public class UDPClient {
    private static final String SEND_FILE_PATH = "/Users/bakitchi/Desktop/InetTest/TCP/upload/book.pdf";

    public static void main(String[] args){
        long startTime = System.currentTimeMillis();

        byte[] buf = new byte[50 * 1024];
        byte[] receiveBuf = new byte[1];

        RandomAccessFile accessFile = null;
        DatagramPacket dpk = null;
        DatagramSocket dsk = null;
        int readSize = -1;
        try {
            accessFile = new RandomAccessFile(SEND_FILE_PATH,"r");
            dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(InetAddress.getByName("192.168.1.103"), UDPUtils.PORT + 1));
            dsk = new DatagramSocket(UDPUtils.PORT, InetAddress.getByName("192.168.1.102"));
            int sendCount = 0;
            while((readSize = accessFile.read(buf,0,buf.length)) != -1){
                dpk.setData(buf, 0, readSize);
                dsk.send(dpk);
//                Thread.sleep(1);
                 //改进丢包
                //等待服务器响应
                {
                    while(true){
                        dpk.setData(receiveBuf, 0, receiveBuf.length);
                        dsk.receive(dpk);

                        //确认服务器正确收到包
                        if(!UDPUtils.isEqualsByteArray(UDPUtils.successData,receiveBuf,dpk.getLength())){
                            System.out.println("Resend ...");
                            dpk.setData(buf, 0, readSize);
                            dsk.send(dpk);
                        }else
                            break;
                    }
                }
//
                System.out.println("Sending No. "+(++sendCount)+"  part!");
            }

            //发送结束信息并等待服务器响应
            while(true){
                System.out.println("Client sending exit message……");
                dpk.setData(UDPUtils.exitData,0,UDPUtils.exitData.length);
                dsk.send(dpk);

                dpk.setData(receiveBuf,0,receiveBuf.length);
                dsk.receive(dpk);

                if(!UDPUtils.isEqualsByteArray(UDPUtils.exitData, receiveBuf, dpk.getLength())){
                    System.out.println("Client resend exit message……");
                    dsk.send(dpk);
                }else
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(accessFile != null)
                    accessFile.close();
                if(dsk != null)
                    dsk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Transmission Time: " + (endTime - startTime) + "ms");
    }
}



