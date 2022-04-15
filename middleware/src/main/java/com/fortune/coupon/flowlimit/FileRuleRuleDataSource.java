package com.fortune.coupon.flowlimit;

import com.alibaba.csp.sentinel.datasource.*;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Component
@Slf4j
public class FileRuleRuleDataSource implements InitFunc {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void init() throws Exception {
        WritableDataSource<List<FlowRule>> wds = new FileWritableDataSource<List<FlowRule>>(createRuleFile(), new Converter<List<FlowRule>, String>() {
            @Override
            public String convert(List<FlowRule> flowRules) {
                return JSON.toJSONString(flowRules);
            }
        });
        //注册
        WritableDataSourceRegistry.registerFlowDataSource(wds);

        ReadableDataSource<String, List<FlowRule>> rds = new FileRefreshableDataSource(createRuleFile(), new Converter<String, List<FlowRule>>() {
            @Override
            public List<FlowRule> convert(String ruleStr) {
                return JSON.parseObject(ruleStr, new TypeReference<List<FlowRule>>() {
                });
            }
        });
        //注册
        FlowRuleManager.register2Property(rds.getProperty());
    }

    private File createRuleFile() throws Exception {
        Path resource = Paths.get(this.getClass().getClassLoader().getResource(".").getPath().substring(1));
        String directoryPath = resource.toAbsolutePath()+"/flowlimit/";
        log.info("directoryPath:{}",directoryPath);
        if(Files.isDirectory(Paths.get(directoryPath))){
           return getFile(directoryPath);
        }else{
            Files.createDirectories(Paths.get(directoryPath));
        }
        return getFile(directoryPath);
    }

    private File getFile(String directoryPath) throws Exception{
        File file = new File(directoryPath+"rule.json");
        if(file.exists()){
            return file;
        }
        file.createNewFile();
        return file;
    }


}
