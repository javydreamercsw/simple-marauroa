package simple.server.extension;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import org.junit.*;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.CommandCenter;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class MonitorExtensionTest {

    public MonitorExtensionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class MonitorExtension.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        MonitorExtension instance = new MonitorExtension();
        CommandCenter.register(null, instance);
    }

    /**
     * Test of onAction method, of class MonitorExtension.
     */
    @Test
    public void testOnAction() {
        System.out.println("onAction");
        ClientObjectInterface monitor = null;
        RPAction action = null;
        MonitorExtension instance = new MonitorExtension();
        instance.onAction((RPObject) monitor, action);
    }

    @Test
    public void testSerialization() {
        RPObject param = new RPObject();
        param.put("name", "object 1");

        RPObject param2 = new RPObject();
        param.put("name", "object 2");

        RPObject[] list = new RPObject[]{param, param2};

        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ObjectOutputStream obj_out = new ObjectOutputStream(bos);
            obj_out.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String encoded = bos.toString();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        System.out.println("The serialized output is: " + encoded);

//DECODE

        RPObject[] paramDecoded;

        String myParam = null;
        try {
            myParam = URLDecoder.decode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        System.out.println("Got parameters");
        ByteArrayInputStream bis = new ByteArrayInputStream(myParam.getBytes());

        try {
            ObjectInputStream obj_in = new ObjectInputStream(bis);

            paramDecoded = (RPObject[]) obj_in.readObject();
            for (RPObject object : paramDecoded) {
                System.out.println(object.get("name"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
