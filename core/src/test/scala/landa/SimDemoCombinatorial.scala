package landa

import spinal.core.sim._
import spinal.core._
import spinal.sim._

import scala.util.Random


object SimDemoCombinatorial {
  class Dut extends Component {
    val io = new Bundle {
      val a, b, c = in UInt (8 bits)
      val result = out UInt (8 bits)
    }
    io.result := io.a + io.b - io.c
  }

  def main(args: Array[String]): Unit = {
    SimConfig.withWave.doSim(rtl = new Dut){ dut =>
      var idx = 0
      while(idx < 100){
        val a, b, c = Random.nextInt(256)
        dut.io.a #= a
        dut.io.b #= b
        dut.io.c #= c
        sleep(1)
        assert(dut.io.result.toInt == ((a+b-c) & 0xFF))
        idx += 1
      }
    }
  }
}



object SimSynchronouExample {
  class Dut extends Component {
    val io = new Bundle {
      val a, b, c = in UInt (8 bits)
      val result = out UInt (8 bits)
    }
    io.result := RegNext(io.a + io.b - io.c) init(0)
  }

  def main(args: Array[String]): Unit = {
    SimConfig.withWave.compile(new Dut).doSim{ dut =>
      dut.clockDomain.forkStimulus(period = 10)

      var idx = 0
      var resultModel = 0
      while(idx < 100) {
        dut.io.a #= Random.nextInt(256)
        dut.io.b #= Random.nextInt(256)
        dut.io.c #= Random.nextInt(256)
        dut.clockDomain.waitActiveEdge()
        assert(dut.io.result.toInt == resultModel)
        resultModel = (dut.io.a.toInt + dut.io.b.toInt - dut.io.c.toInt) & 0xFF
        idx += 1
      }
    }
  }
}

import SimSynchronouExample.Dut
object DutTestbench extends App{
  val compiled = SimConfig.withWave.compile(rtl = new Dut)

  compiled.doSim("test1"){ dut =>
    //Testbench code
  }

  compiled.doSim("test2"){ dut =>
    //Testbench code
  }
}



object Miaou5252 extends App{
  SimConfig.withWave.compile(new Dut).doSim{ dut =>
    dut.clockDomain.forkStimulus(period = 10)

    var idx = 0
    var resultModel = 0
    while(idx < 100) {
      dut.io.a #= Random.nextInt(256)
      dut.io.b #= Random.nextInt(256)
      dut.io.c #= Random.nextInt(256)
      dut.clockDomain.waitActiveEdge()
      assert(dut.io.result.toInt == resultModel)
      resultModel = (dut.io.a.toInt + dut.io.b.toInt - dut.io.c.toInt) & 0xFF
      idx += 1
    }
  }
}