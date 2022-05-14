import org.springframework.util.DigestUtils;

public class TestMd5 {
    public static void main(String[] args) {
        String password="123456";
        String password1=new String(DigestUtils.md5DigestAsHex(password.getBytes()));
        System.out.println(password1);
    }
}
