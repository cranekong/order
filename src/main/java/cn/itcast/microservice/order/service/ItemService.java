package cn.itcast.microservice.order.service;

import cn.itcast.microservice.order.feign.ItemFeignClient;
import cn.itcast.microservice.order.pojo.Item;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ItemService {

    // Spring框架对RESTful方式的http请求做了封装，来简化操作
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ItemFeignClient itemFeignClient;

//  初始版本
    /*public Item queryItemById(Long id) {
        String serviceId = "itcast-microservice-item";
        List<ServiceInstance> instances = this.discoveryClient.getInstances(serviceId);
        if (instances.isEmpty()) {
            return null;
        }
        // 为了演示，在这里只获取一个实例
        ServiceInstance serviceInstance = instances.get(0);
        String url = serviceInstance.getHost() + ":" + serviceInstance.getPort();
        return this.restTemplate.getForObject("http://" + url + "/item/" + id, Item.class);
    }*/

// 升级版本
    /*@HystrixCommand(fallbackMethod = "queryItemByIdFallbackMethod") // 进行容错处理
     public Item queryItemById(Long id) {
        String serviceId = "itcast-microservice-item";
        return this.restTemplate.getForObject("http://" + serviceId + "/item/" + id, Item.class);
    }*/

    // 再次升级
    @HystrixCommand(fallbackMethod = "queryItemByIdFallbackMethod") // 进行容错处理
    public Item queryItemById(Long id) {
        return this.itemFeignClient.queryItemById(id);
    }

    public Item queryItemByIdFallbackMethod(Long id){ // 请求失败执行的方法
        return new Item(id, "查询商品信息出错!", null, null, null);
    }
}
