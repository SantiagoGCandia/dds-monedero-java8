package dds.monedero.model;

import java.time.LocalDate;

public class Deposito extends Movimiento{

  public Deposito(LocalDate fecha, double monto){
    super(fecha, monto);
  }

  public boolean fueDepositado(LocalDate fecha){
    return esDeLaFecha(fecha);
  }

  public boolean isDeposito(){
    return true;
  }

  public double calcularValor(Cuenta cuenta){
      return cuenta.getSaldo() + getMonto();
  }
}
