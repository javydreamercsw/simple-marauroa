package utilities.RPClass;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import simple.SimpleServerT;
import simple.server.core.entity.item.Item;

public class ItemTestHelperTest extends SimpleServerT{

    @Test
    public void testcreateItem() throws Exception {
        ItemTestHelper.createItem();
        final Item item = ItemTestHelper.createItem("blabla");
        assertEquals("blabla", item.getName());
    }
}
