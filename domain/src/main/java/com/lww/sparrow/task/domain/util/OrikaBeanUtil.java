package com.lww.sparrow.task.domain.util;

import com.lww.sparrow.task.domain.bean.JobExecuteLogBean;
import com.lww.sparrow.task.domain.bean.JobQueueBean;
import com.lww.sparrow.task.domain.entity.JobInfo;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class OrikaBeanUtil {


    private MapperFacade mapperFacade = null;

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();


    public <V, P> P convert(V base, Class<P> target) {
        return base == null ? null : mapperFacade.map(base, target);
    }

    public <V, P> List<P> convertList(List<V> baseList, Class<P> target) {
        return baseList == null ? null : mapperFacade.mapAsList(baseList, target);
    }


    @PostConstruct
    public void init() {
        // 初始化
        mapperFacade = mapperFactory.getMapperFacade();

        mapperFactory.classMap(JobQueueBean.class, JobExecuteLogBean.class)
                .field("id", "jobId")
                .field("executeDate", "expressionDate")
                .byDefault().register();

    }


}
