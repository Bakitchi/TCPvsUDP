package TCP.server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Title: TCPServer.java
 * @Package TCP.server
 * @Description:
 * @Author: Bakitchi
 * @Date: 2018/5/12下午9:59
 */
public class TCPServer extends Thread{
    private int port; //服务器端口
    private String dir; //保存目录
    private ServerSocket serverSocket;
    private Socket socket;

    //构造函数，设置dir与port，创建ServerSocket对象
    public TCPServer(String dir, int port) throws Exception{
        this.dir = dir;
        this.port = port;
        serverSocket = new ServerSocket(port);
        System.out.println("TCP Server started!");
    }


    @Override
    public void run(){
        DataInputStream input = null;  //socket监听输入流
        DataOutputStream fileOutput = null; //文件输出流
        DataOutputStream ack = null; //确认信号->Client
        int bufferSize = 8192;  //设置缓冲区大小

        byte[] buffer = new byte[bufferSize];
        long doneLen = 0;  //已经传输完成的数据长度
        long fileLen = 0;  //文件长度

        try {
            socket = null;
            System.out.println("Waiting for connection……");
            while(true) {
                socket = serverSocket.accept();
                System.out.println("Client " + socket.getInetAddress() + " connected!");

                input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                //构建存储地址
                String fileDir = dir + socket.getInetAddress().toString().substring(1,socket.getInetAddress().toString().length());

                System.out.println(fileDir);
                File file = new File(fileDir);
                if (!file.exists()) {
                    file.mkdir();
                }

                Long startTime = input.readLong();
                String fileName = input.readUTF();
                String filePath = fileDir + "/" +fileName;

                file = new File(filePath);
                if (!file.exists()) {
                    file.createNewFile();
                }

                fileOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                fileLen = input.readLong();

                System.out.println("File Length: " + fileLen + " bytes\n");
                System.out.println("Transmission begin……" + "\n");
                ack = new DataOutputStream(socket.getOutputStream());

                while(true) {
                    int read = 0;
                    if (input != null) {
                        read = input.read(buffer);
                        ack.writeUTF("Finished");
                    }

                    if (read == -1){
                        break;
                    }
                    doneLen += read;

                    fileOutput.write(buffer, 0, read);
                }

                if (doneLen == fileLen) {
                    System.out.println("Transmission complete, file saved as " + file +"\n");
                    long durTime = System.currentTimeMillis() - startTime;
                    System.out.println("Transmission Time: " + durTime + " ms");
                } else {
                    System.out.println(fileName + " from " + socket.getInetAddress() + " lost while transmitting!");
                    file.delete();
                }
                ack.close();
                input.close();
                fileOutput.close();


            }
        } catch (Exception e) {
            System.out.println("Error occurs when transporting data! \n");
            e.printStackTrace();
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        TCPServer server = new TCPServer("/home/bakitchi/Desktop/Test/TCP/", 8088);
        server.start();
    }


}
