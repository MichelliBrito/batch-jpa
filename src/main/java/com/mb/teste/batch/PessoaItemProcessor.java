package com.mb.teste.batch;

import org.springframework.batch.item.ItemProcessor;

public class PessoaItemProcessor implements ItemProcessor<Pessoa, Pessoa> {

    @Override
    public Pessoa process(Pessoa pessoa) throws Exception {
        System.out.println("Entrou no processor");
        final String firstName = pessoa.getNome().toUpperCase();
        final String lastName = pessoa.getSobrenome().toUpperCase();

        final Pessoa transformed = new Pessoa(firstName, lastName);

        System.out.println("Converting (" + pessoa + ") into (" + transformed + ")");

        return transformed;
    }
}
