package TCP.client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

/**
 * @Title: TCPClient.java
 * @Package TCP.client
 * @Description:
 * @Author: Bakitchi
 * @Date: 2018/5/12下午9:58
 */
public class TCPClient {
    private String dir;  //上传文件的存储目录
    private String host; //服务器主机IP地址
    private int port;   //服务器端口号

    public TCPClient(String dir, String host, int port) {
        this.dir = dir;
        this.host = host;
        this.port = port;
    }

    public void uploadFile(String fileName) {
        Socket socket;

        DataInputStream input;
        DataOutputStream output;
        DataInputStream getAck;  //获得服务器的ACK

        int bufferSize = 5 * 1024;  //设置缓冲区大小
        byte[] buf = new byte[bufferSize];

        try{

            socket = new Socket(host, port);//设置socket，并进行连接connect

            String filePath = dir + fileName;
            File file = new File(filePath);
            System.out.println("File Length: " + (int) file.length() + " bytes");

            input = new DataInputStream(new FileInputStream(filePath));
            output = new DataOutputStream(socket.getOutputStream());//将socket设置为数据的传输出口

            getAck = new DataInputStream(socket.getInputStream());//设置socket数据的来源

            //传输开始时间
            output.writeLong(System.currentTimeMillis());
            output.flush();
            //传输文件名
            output.writeUTF(file.getName());
            output.flush();
            //传输文件长度
            output.writeLong((long) file.length());
            output.flush();

            int readSize = 0;

            while(true) {
                if(input != null) {
                    readSize = input.read(buf);
                }
                if(readSize == -1)
                    break;

                output.write(buf, 0, readSize);

                if(!getAck.readUTF().equals("Finished")) {
                    System.out.println("Server "+ host + ":" + port + " lost connection！");
                    break;
                }
            }
            output.flush();
            input.close();
            output.close();
            socket.close();
            getAck.close();
            System.out.println("Transmission complete！");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TCPClient client = new TCPClient("/Users/bakitchi/Desktop/InetTest/TCP/upload/", "192.168.1.103", 8088);
        client.uploadFile("book.pdf");
    }
}
