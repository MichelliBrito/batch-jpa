package com.mb.teste.batch;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Autowired
    PessoaRepository pessoaRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Entrou no listener");
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            System.out.println("!!! JOB FINISHED! Time to verify the results");

            List<Pessoa> pessoas = pessoaRepository.findAll();
            for(Pessoa pessoa : pessoas){
                System.out.println(pessoa.getNome());
            }
       }
    }
}
