# TCPvsUDP
## A Java Project for Socket Programming Exercise
Including TCP & UDP server, TCP & UDP client.

For Computer Networking Course.

### For UDP transmission, to reduce the ratio of packet loss, the project introduced 3 ways of implements:
1. Shrink the bytes for the segments to be sent.
2. Add a tiny delay after sending a segment.
3. To inform the client the server has received the segment successfully, using the <code>UDPUtils.successData</code>
