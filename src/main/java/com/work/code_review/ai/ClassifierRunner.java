package com.work.code_review.ai;

import com.work.code_review.CodeReviewApplication;
import com.work.code_review.ai.service.ClassifierService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class ClassifierRunner {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("用法: java Task1ClassifierRunner <输入文件> <输出文件>");
            System.exit(1);
        }

        ConfigurableApplicationContext context = new SpringApplicationBuilder(CodeReviewApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        try {
            ClassifierService task1ClassifierService = context.getBean(ClassifierService.class);
            task1ClassifierService.batchClassify(args[0], args[1]);
        } finally {
            context.close();
        }
    }
}
