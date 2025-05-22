package estrutura;

import model.RegistroClimatico;

public class Lista {
    private RegistroClimatico inicio;

    public void inserir(RegistroClimatico r) {
        if (inicio == null) {
            inicio = r;
        } else {
            RegistroClimatico atual = inicio;
            while (atual.getProximo() != null) {
                atual = atual.getProximo();
            }
            atual.setProximo(r);
        }
    }

    public RegistroClimatico buscar(int id) {
        RegistroClimatico atual = inicio;
        while (atual != null) {
            if (atual.getIdRegistro() == id) return atual;
            atual = atual.getProximo();
        }
        return null;
    }

    public boolean remover(int id) {
        if (inicio == null) return false;
        if (inicio.getIdRegistro() == id) {
            inicio = inicio.getProximo();
            return true;
        }
        RegistroClimatico atual = inicio;
        while (atual.getProximo() != null) {
            if (atual.getProximo().getIdRegistro() == id) {
                atual.setProximo(atual.getProximo().getProximo());
                return true;
            }
            atual = atual.getProximo();
        }
        return false;
    }

    public void listar() {
        RegistroClimatico atual = inicio;
        while (atual != null) {
            System.out.println(atual);
            atual = atual.getProximo();
        }
    }

    public int contar() {
        int contador = 0;
        RegistroClimatico atual = inicio;
        while (atual != null) {
            contador++;
            atual = atual.getProximo();
        }
        return contador;
    }

    public RegistroClimatico getInicio() {
        return inicio;
    }

    public int buscarContandoPassos(int id) {
        RegistroClimatico atual = inicio;
        int passos = 0;
        while (atual != null) {
            passos++;
            if (atual.getIdRegistro() == id) return passos;
            atual = atual.getProximo();
        }
        return -1; // não encontrado
    }

}
