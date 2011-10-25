package utilities.RPClass;

import static org.junit.Assert.assertEquals;
import simple.SimpleServerTest;
import simple.server.core.entity.item.Item;

public class ItemTestHelperTest extends SimpleServerTest{

    @org.junit.Test
    public void testcreateItem() throws Exception {
        init();
        ItemTestHelper.createItem();
        final Item item = ItemTestHelper.createItem("blabla");
        assertEquals("blabla", item.getName());
    }
}
