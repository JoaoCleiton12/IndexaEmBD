package model;

import java.time.LocalDateTime;

public class RegistroClimatico {
    private int idRegistro;
    private String idDispositivo;
    private LocalDateTime dataHora;
    private double temperatura;
    private double umidade;
    private double pressao;
    private RegistroClimatico proximo;

    public RegistroClimatico(int id, String dispositivo, double temp, double umi, double pres) {
        this.idRegistro = id;
        this.idDispositivo = dispositivo;
        this.temperatura = temp;
        this.umidade = umi;
        this.pressao = pres;
        this.dataHora = LocalDateTime.now();
    }


    public int getIdRegistro() { return idRegistro; }
    public String getIdDispositivo() { return idDispositivo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public double getTemperatura() { return temperatura; }
    public double getUmidade() { return umidade; }
    public double getPressao() { return pressao; }
    public RegistroClimatico getProximo() { return proximo; }

    // Setters
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }
    public void setUmidade(double umidade) { this.umidade = umidade; }
    public void setPressao(double pressao) { this.pressao = pressao; }
    public void setProximo(RegistroClimatico proximo) { this.proximo = proximo; }

    @Override
    public String toString() {
        return String.format("ID: %d | Dispositivo: %s | Temp: %.2f | Umidade: %.2f | Pressão: %.2f | Hora: %s",
                idRegistro, idDispositivo, temperatura, umidade, pressao, dataHora.toString());
    }
}
