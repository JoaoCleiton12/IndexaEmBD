package estrutura;

import model.RegistroClimatico;

class NoAVL {
    private int chave;
    private RegistroClimatico referencia;
    private NoAVL esquerda, direita;
    private int altura;

    public NoAVL(int chave, RegistroClimatico referencia) {
        this.chave = chave;
        this.referencia = referencia;
        this.altura = 1;
    }


    public int getChave() { return chave; }
    public RegistroClimatico getReferencia() { return referencia; }
    public NoAVL getEsquerda() { return esquerda; }
    public NoAVL getDireita() { return direita; }
    public int getAltura() { return altura; }

    public void setChave(int chave) { this.chave = chave; }
    public void setReferencia(RegistroClimatico referencia) { this.referencia = referencia; }
    public void setEsquerda(NoAVL esquerda) { this.esquerda = esquerda; }
    public void setDireita(NoAVL direita) { this.direita = direita; }
    public void setAltura(int altura) { this.altura = altura; }

}