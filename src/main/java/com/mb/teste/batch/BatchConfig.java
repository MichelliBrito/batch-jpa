package com.mb.teste.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

    @Configuration
    @EnableBatchProcessing
    public class BatchConfig {

        private final JobBuilderFactory jobBuilderFactory;

        private final StepBuilderFactory stepBuilderFactory;

        @Autowired
        PessoaRepository pessoaRepository;

        //private final DataSource dataSource;

        public BatchConfig(JobBuilderFactory jobBuilderFactory,
                           StepBuilderFactory stepBuilderFactory) {
            // DataSource dataSource) {
            this.jobBuilderFactory = jobBuilderFactory;
            this.stepBuilderFactory = stepBuilderFactory;
            //  this.dataSource = dataSource;
        }

        @Bean
        public FlatFileItemReader<Pessoa> reader(){
            System.out.println("Entrou no reader");
            FlatFileItemReader<Pessoa> reader = new FlatFileItemReader<>();
            reader.setResource(new ClassPathResource("pessoa.csv"));
            reader.setLineMapper(new DefaultLineMapper<Pessoa>() {
                {
                    setLineTokenizer(new DelimitedLineTokenizer() {
                        {
                            setNames(new String[]{"nome", "sobrenome"});
                        }
                    });
                    setFieldSetMapper(new BeanWrapperFieldSetMapper<Pessoa>() {
                        {
                            setTargetType(Pessoa.class);
                        }
                    });
                }
            });
            return reader;
        }


        @Bean
        public PessoaItemProcessor processor() {
            return new PessoaItemProcessor();
        }

        @Bean
        public RepositoryItemWriter<Pessoa> writer() {
            System.out.println("Entrou no writer");
            RepositoryItemWriter<Pessoa> writer = new RepositoryItemWriter<>();
            writer.setRepository(pessoaRepository);
            writer.setMethodName("save");
            System.out.println("Salvou pessoa");
            return writer;
        }

        @Bean
        public Job importPessoaJob(JobCompletionNotificationListener listener) {
            return jobBuilderFactory.get("importPessoaJob")
                    .incrementer(new RunIdIncrementer())
                    .listener(listener)
                    .flow(step1())
                    .end()
                    .build();
        }

        @Bean
        public Step step1() {
            return stepBuilderFactory.get("step1")
                    .<Pessoa, Pessoa>chunk(10)
                    .reader(reader())
                    .processor(processor())
                    .writer(writer())
                    .build();
        }

}
