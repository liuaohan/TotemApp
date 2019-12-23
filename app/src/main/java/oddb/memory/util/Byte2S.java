package oddb.memory.util;

public class Byte2S {

    //编码字符串为byte
    public static  byte[] str2Bytes(String s, int length) {
        byte[] ret = new byte[length];
        byte[] temp = s.getBytes();
        if (temp.length >= length) {
            for (int i = 0; i < length; i++) {
                ret[i] = temp[i];
            }
            return ret;
        } else {
            for (int i = 0; i < temp.length; i++) {
                ret[i] = temp[i];
            }
            for (int i = temp.length; i < length; i++) {
                ret[i] = (byte) 32;
            }
            return ret;
        }
    }

    //解码byte为字符串
    public static String byte2str(byte[] b, int off, int len) {
        String s;
        int k = 0;
        for (int i = off; i < off + len; i++) {
            if (b[i] != 32) {
                k++;
            } else {
                break;
            }
        }
        s = new String(b, off, k);
        return s;
    }

    //编码int为byte
    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte) (value >> 8 * i);
        }
        return b;
    }

    //解码byte为int
    public static int bytes2Int(byte[] b, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = b[i] & 0xff;
            n <<= (--len) * 8;
            sum += n;
        }
        return sum;
    }

}
