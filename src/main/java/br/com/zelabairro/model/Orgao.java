package br.com.zelabairro.model;

//tb_orgao: equipe que executa o servico (so leitura na API, vem do seed)
public class Orgao {

    private int id;
    private String nome;
    private String email;
    private String telefone;

    public Orgao() {
    }

    public Orgao(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
