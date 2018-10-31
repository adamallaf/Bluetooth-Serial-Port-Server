package allaf.bluetoothserialportserver;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void unicode_decode_encode() {
        String arabic = "هذه مكتوبة بالعربية!";
        String polish = "Będzie działać! ążźćę€łń";
        byte[] bArabic = arabic.getBytes(Charset.defaultCharset());
        String bArabicToStr = new String(bArabic, Charset.defaultCharset());
        try {
            System.out.write(bArabic);
            System.out.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertArrayEquals(arabic.toCharArray(), bArabicToStr.toCharArray());
        byte[] bPolish = polish.getBytes(Charset.defaultCharset());
        String bPolishToStr = new String(bPolish, Charset.defaultCharset());
        assertArrayEquals(polish.toCharArray(), bPolishToStr.toCharArray());
        try {
            System.out.write(bPolish);
            System.out.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void unicode_fakap() {
        int be = 0;
        int le = 0;
        int tmp = 0;
        String fk = "ABCDEF12345\n" +
                "هذه مكتوبة بالعربية\n" +
                "będzie działać\n" +
                "����������������������������������������";
        System.out.println(fk);
        byte[] bb = fk.getBytes(Charset.defaultCharset());
        for (byte b: bb) {
            System.out.print(b);
            System.out.print(',');
        }
        System.out.println();
        for (int i = 0; i < Array.getLength(bb) - 1; i += 2) {
            if(bb[i] > 127 || bb[i] < 0) {
                be = ((-10 * ((int)bb[i] + (int)bb[i + 1])));
                le = (int)bb[i + 1] & 0x0f;
                tmp = be | le;
                System.out.print(String.format("0x%04x ", tmp));
                System.out.print(String.format("%d ", be));
                System.out.print(',');
            }
            else {
                System.out.print((char) bb[i]);
                System.out.print(',');
                System.out.print((char) bb[i + 1]);
                System.out.print(',');
            }
        }
        System.out.println();
        try {
            System.out.write(new Integer(Array.getLength(bb)).toString().getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.write('\n');
        String bToStr = new String(bb, Charset.defaultCharset());
        System.out.print(bToStr);
        for (char x: bToStr.toCharArray()) {
            System.out.print(String.format("%c 0x%04X", x, (int)x));
            System.out.println();
        }
    }
}