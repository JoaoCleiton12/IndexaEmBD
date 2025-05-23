package estrutura;

import model.RegistroClimatico;

import java.io.PrintWriter;

public class ArvoreAVL {
    private NoAVL raiz;
    private String ultimaRotacao = "nenhuma";

    public void inserir(int chave, RegistroClimatico referencia) {
        raiz = inserir(raiz, chave, referencia);
    }

    private NoAVL inserir(NoAVL no, int chave, RegistroClimatico referencia) {
        if (no == null) return new NoAVL(chave, referencia);

        if (chave < no.getChave()) {
            no.setEsquerda(inserir(no.getEsquerda(), chave, referencia));
        } else if (chave > no.getChave()) {
            no.setDireita(inserir(no.getDireita(), chave, referencia));
        } else {
            ultimaRotacao = "nenhuma";
            return no; // chave duplicada
        }

        atualizarAltura(no);
        return balancear(no);
    }

    public RegistroClimatico buscar(int chave) {
        NoAVL no = buscarNo(raiz, chave);
        return (no != null) ? no.getReferencia() : null;
    }

    private NoAVL buscarNo(NoAVL no, int chave) {
        if (no == null) return null;
        if (chave == no.getChave()) return no;
        return (chave < no.getChave()) ? buscarNo(no.getEsquerda(), chave) : buscarNo(no.getDireita(), chave);
    }

    public void remover(int chave) {
        raiz = remover(raiz, chave);
    }

    private NoAVL remover(NoAVL no, int chave) {
        if (no == null) return null;

        if (chave < no.getChave()) {
            no.setEsquerda(remover(no.getEsquerda(), chave));
        } else if (chave > no.getChave()) {
            no.setDireita(remover(no.getDireita(), chave));
        } else {
            if (no.getEsquerda() == null || no.getDireita() == null) {
                no = (no.getEsquerda() != null) ? no.getEsquerda() : no.getDireita();
            } else {
                NoAVL sucessor = getMin(no.getDireita());
                no.setChave(sucessor.getChave());
                no.setReferencia(sucessor.getReferencia());
                no.setDireita(remover(no.getDireita(), sucessor.getChave()));
            }
        }

        if (no == null) return null;

        atualizarAltura(no);
        return balancear(no);
    }

    private NoAVL getMin(NoAVL no) {
        while (no.getEsquerda() != null) no = no.getEsquerda();
        return no;
    }

    private void atualizarAltura(NoAVL no) {
        int altEsq = (no.getEsquerda() != null) ? no.getEsquerda().getAltura() : 0;
        int altDir = (no.getDireita() != null) ? no.getDireita().getAltura() : 0;
        no.setAltura(1 + Math.max(altEsq, altDir));
    }

    private int fatorBalanceamento(NoAVL no) {
        int altEsq = (no.getEsquerda() != null) ? no.getEsquerda().getAltura() : 0;
        int altDir = (no.getDireita() != null) ? no.getDireita().getAltura() : 0;
        return altEsq - altDir;
    }

    private NoAVL balancear(NoAVL no) {
        int fb = fatorBalanceamento(no);

        if (fb > 1) {
            if (fatorBalanceamento(no.getEsquerda()) < 0) {
                no.setEsquerda(rotacionarEsquerda(no.getEsquerda()));
                ultimaRotacao = "rotação dupla direita";
            } else {
                ultimaRotacao = "rotação simples direita";
            }
            return rotacionarDireita(no);
        }

        if (fb < -1) {
            if (fatorBalanceamento(no.getDireita()) > 0) {
                no.setDireita(rotacionarDireita(no.getDireita()));
                ultimaRotacao = "rotação dupla esquerda";
            } else {
                ultimaRotacao = "rotação simples esquerda";
            }
            return rotacionarEsquerda(no);
        }

        ultimaRotacao = "nenhuma";
        return no;
    }

    private NoAVL rotacionarDireita(NoAVL y) {
        NoAVL x = y.getEsquerda();
        NoAVL T2 = x.getDireita();
        x.setDireita(y);
        y.setEsquerda(T2);
        atualizarAltura(y);
        atualizarAltura(x);
        return x;
    }

    private NoAVL rotacionarEsquerda(NoAVL x) {
        NoAVL y = x.getDireita();
        NoAVL T2 = y.getEsquerda();
        y.setEsquerda(x);
        x.setDireita(T2);
        atualizarAltura(x);
        atualizarAltura(y);
        return y;
    }

    public int getAltura() {
        return (raiz != null) ? raiz.getAltura() : 0;
    }

    public String getUltimaRotacao() {
        return ultimaRotacao;
    }


    public void emOrdem(PrintWriter out) {
        emOrdem(raiz, out);
    }

    private void emOrdem(NoAVL no, PrintWriter out) {
        if (no != null) {
            emOrdem(no.getEsquerda(), out);
            out.println("ID: " + no.getChave() + " → " + no.getReferencia());
            emOrdem(no.getDireita(), out);
        }
    }

    public int buscarContandoPassos(int chave) {
        return contarPassos(raiz, chave, 1);
    }

    private int contarPassos(NoAVL no, int chave, int passos) {
        if (no == null) return -1;
        if (chave == no.getChave()) return passos;
        if (chave < no.getChave()) return contarPassos(no.getEsquerda(), chave, passos + 1);
        else return contarPassos(no.getDireita(), chave, passos + 1);
    }

}
