import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.utils.JsonUtils;
import org.junit.Test;

import java.util.List;

/*@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)*/
public class transTest {
   /* @Autowired
    private StuService stuService;

    @Autowired
    private TransService transService;*/

    /*@Test
    public void testTrans(){
        transService.save();
    }*/
    @Test
    public void testJson(){
        String shopcartJson = "[{\"itemId\":\"cake-1004\",\"itemImgUrl\":\"http://122.152.205.72:88/foodie/cake-1004/img1.png\",\"itemName\":\"【天天吃货】美味沙琪玛 超棒下午茶\",\"specId\":\"cake-1004-spec-1\",\"specName\":\"巧克力\",\"buyCounts\":3,\"priceDiscount\":\"14400\",\"priceNormal\":\"16000\"},{\"itemId\":\"meat-1006\",\"itemImgUrl\":\"http://122.152.205.72:88/foodie/meat-1006/img1.png\",\"itemName\":\"【天天吃货】烤肠 猪肉牛肉鸡肉 肉类最佳零食\",\"specId\":\"meat-1006-spec-1\",\"specName\":\"鸡肉\",\"buyCounts\":3,\"priceDiscount\":\"14310\",\"priceNormal\":\"15900\"}]";
        List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
        System.out.println(shopcartList);
    }
}
