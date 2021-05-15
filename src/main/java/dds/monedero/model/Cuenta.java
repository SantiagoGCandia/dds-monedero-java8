package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public void poner(double cuanto) {
    validarMonto(cuanto);
    exede3DepositosDiarios();
    agregarMovimiento(new Deposito(LocalDate.now(), cuanto));
  }

  public void sacar(double cuanto) {
    validarMonto(cuanto);
    saldoNoDisponible(cuanto);
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    exedeLimiteDeExtraccion(cuanto , limite);
    agregarMovimiento(new Extraccion(LocalDate.now(), cuanto));
  }

  public void agregarMovimiento( Movimiento movimiento) {
    movimientos.add(movimiento);
    setSaldo(movimiento.calcularValor(this));
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  private void saldoNoDisponible(double cuanto) {
    if ( getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  private void exedeLimiteDeExtraccion(double cuanto, double limite) {
    if ( cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  private void validarMonto(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  private void exede3DepositosDiarios() {
    if (getMovimientos().stream().filter(Movimiento::isDeposito).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }
}
