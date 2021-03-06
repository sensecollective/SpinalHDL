package spinal.tester.scalatest

import org.scalatest.FunSuite
import spinal.core._
import spinal.sim._
import spinal.core.sim._
import spinal.tester.scalatest.SpinalSimVerilatorIoTest.SpinalSimVerilatorIoTestTop

import scala.concurrent.{Await, Future}
import scala.util.Random

object SpinalSimVerilatorIoTest{
  object State extends SpinalEnum{
    val A,B,C,D,E = newElement()
  }

  class newEnumTest(encoding : SpinalEnumEncoding) extends Area{
    val stateInput = in(State(encoding))
    val stateOutput = out(State(encoding))
    val stateDecoded = out(Bits(5 bits))

    stateDecoded := stateInput.mux[Bits](
      State.A -> 1,
      State.B -> 2,
      State.C -> 4,
      State.D -> 8,
      State.E -> 16
    )
    stateOutput := stateInput
  }

  class SpinalSimVerilatorIoTestTop extends Component {
    val io = new Bundle {
      val bool = in Bool
      val u1  = in UInt (1 bits)
      val u8  = in UInt (8 bits)
      val u16 = in UInt (16 bits)
      val u31 = in UInt (31 bits)
      val u32 = in UInt (32 bits)
      val u63 = in UInt (63 bits)
      val u64 = in UInt (64 bits)
      val u65 = in UInt (65 bits)
      val u127 = in UInt (127 bits)
      val u128 = in UInt (128 bits)
      val s1  = in SInt (1 bits)
      val s8  = in SInt (8 bits)
      val s16 = in SInt (16 bits)
      val s31 = in SInt (31 bits)
      val s32 = in SInt (32 bits)
      val s63 = in SInt (63 bits)
      val s64 = in SInt (64 bits)
      val s65 = in SInt (65 bits)
      val s127 = in SInt (127 bits)
      val s128 = in SInt (128 bits)
    }


    val sub = new Component{
      val x = False
      val subsub = new Component{
        val x = False
      }
    }

    val miaou = out(Reg(Bits(128 bits)))
    miaou := 42
    val nativeEncoding = new newEnumTest(native)
    val binarySequentialEncoding =new newEnumTest(binarySequential)
    val binaryOneHotEncoding = new newEnumTest(binaryOneHot)
  }
}

class SpinalSimVerilatorIoTest extends FunSuite {
  var compiled : SimCompiled[SpinalSimVerilatorIoTestTop] = null
  def doTest: Unit ={
    compiled.doSim{ dut =>
      def checkBoolean(value : Boolean, that : Bool): Unit@suspendable ={
        that #= value
        sleep(1)
        assert(that.toBoolean == value, that.getName() + " " + value)
      }

      def checkInt(value : Int, that : BitVector): Unit@suspendable ={
        that #= value
        sleep(1)
        assert(that.toInt == value, that.getName() + " " + value)
      }

      def checkLong(value : Long, that : BitVector): Unit@suspendable ={
        that #= value
        sleep(1)
        assert(that.toLong == value, that.getName() + " " + value)
      }

      def checkBigInt(value : BigInt, that : BitVector): Unit@suspendable ={
        that #= value
        sleep(1)
        assert(that.toBigInt == value, that.getName() + " " + value)
      }


      (0 to 20).suspendable.foreach { e =>
        List(false, true).suspendable.foreach(value => checkBoolean(value, dut.io.bool))

        //checkInt
        List(0, 1).suspendable.foreach(value => checkInt(value, dut.io.u1))
        List(0, 1, 127, 255).suspendable.foreach(value => checkInt(value, dut.io.u8))
        List(0, 1, 0xFFFF).suspendable.foreach(value => checkInt(value, dut.io.u16))
        List(0, 1, 0x7FFFFFFF).suspendable.foreach(value => checkInt(value, dut.io.u31))

        List(0, -1).suspendable.foreach(value => checkInt(value, dut.io.s1))
        List(0, 1, -1, 127, -128).suspendable.foreach(value => checkInt(value, dut.io.s8))
        List(0, 1, -1, Short.MaxValue, Short.MinValue).suspendable.foreach(value => checkInt(value, dut.io.s16))
        List(0, 1, -1, 0xFFFFFFFF, -1, Int.MaxValue, Int.MinValue).suspendable.foreach(value => checkInt(value, dut.io.s32))

        //checkLong
        List(0, 1).suspendable.foreach(value => checkLong(value, dut.io.u1))
        List(0, 1, 127, 255).suspendable.foreach(value => checkLong(value, dut.io.u8))
        List(0, 1, 0xFFFF).suspendable.foreach(value => checkLong(value, dut.io.u16))
        List(0, 1, 0x7FFFFFFF).suspendable.foreach(value => checkLong(value, dut.io.u32))
        List(0l, 1l, 0x7FFFFFFFFFFFFFFFl).suspendable.foreach(value => checkLong(value, dut.io.u63))

        List(0, -1).suspendable.foreach(value => checkLong(value, dut.io.s1))
        List(0, 1, -1, 127, -128).suspendable.foreach(value => checkLong(value, dut.io.s8))
        List(0, 1, -1, Short.MaxValue, Short.MinValue).suspendable.foreach(value => checkLong(value, dut.io.s16))
        List(0, 1, -1, 0xFFFFFFFF, -1, Int.MaxValue, Int.MinValue).suspendable.foreach(value => checkLong(value, dut.io.s32))
        List(0l, 1l, 0xFFFFFFFFFFFFFFFFl, -1l, Long.MaxValue, Long.MinValue).suspendable.foreach(value => checkLong(value, dut.io.s64))

        //checkBigInt
        List(0, 1).suspendable.foreach(value => checkBigInt(value, dut.io.u1))
        List(0, 1, 127, 255).suspendable.foreach(value => checkBigInt(value, dut.io.u8))
        List(0, 1, 0xFFFF).suspendable.foreach(value => checkBigInt(value, dut.io.u16))
        List(0, 1, 0x7FFFFFFF).suspendable.foreach(value => checkBigInt(value, dut.io.u32))
        List(0l, 1l, 0x7FFFFFFFFFFFFFFFl).suspendable.foreach(value => checkBigInt(value, dut.io.u63))

        List(0, -1).suspendable.foreach(value => checkBigInt(value, dut.io.s1))
        List(0, 1, -1, 127, -128).suspendable.foreach(value => checkBigInt(value, dut.io.s8))
        List(0, 1, -1, Short.MaxValue, Short.MinValue).suspendable.foreach(value => checkBigInt(value, dut.io.s16))
        List(0, 1, -1, 0xFFFFFFFF, -1, Int.MaxValue, Int.MinValue).suspendable.foreach(value => checkBigInt(value, dut.io.s32))
        List(0l, 1l, 0xFFFFFFFFFFFFFFFFl, -1l, Long.MaxValue, Long.MinValue).suspendable.foreach(value => checkBigInt(value, dut.io.s64))

        forkJoin(
          () => Random.shuffle((0 to 1 )).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.u1)),
          () => Random.shuffle((0 to 8 )).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.u8)),
          () => Random.shuffle((0 to 16 )).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.u16)),
          () => Random.shuffle((0 to 31 )).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.u31)),
          () => Random.shuffle((0 to 32 )).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.u32)),
          () => Random.shuffle((0 to 63)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.u63)),
          () => Random.shuffle((0 to 64)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.u64)),
          () => Random.shuffle((0 to 65)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.u65)),
          () => Random.shuffle((0 to 127)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.u127)),
          () => Random.shuffle((0 to 128)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.u128)),
          () => Random.shuffle((0 to 1  - 1)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.s1)),
          () => Random.shuffle((0 to 8  - 1)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.s8)),
          () => Random.shuffle((0 to 16 - 1 )).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.s16)),
          () => Random.shuffle((0 to 31 - 1 )).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.s31)),
          () => Random.shuffle((0 to 32 - 1 )).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.s32)),
          () => Random.shuffle((0 to 62)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.s63)),
          () => Random.shuffle((0 to 63)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.s64)),
          () => Random.shuffle((0 to 64)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.s65)),
          () => Random.shuffle((0 to 126)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.s127)),
          () => Random.shuffle((0 to 127)).map(n => BigInt("0" + "1" * n, 2)).suspendable.foreach(value => checkBigInt(value, dut.io.s128))
        )

        forkJoin(
          () => Random.shuffle((0 to 1  - 1)).map(n => -BigInt("0" + "1" * n, 2) -1).suspendable.foreach(value => checkBigInt(value, dut.io.s1 )),
          () => Random.shuffle((0 to 8  - 1)).map(n => -BigInt("0" + "1" * n, 2) -1).suspendable.foreach(value => checkBigInt(value, dut.io.s8 )),
          () => Random.shuffle((0 to 16 - 1 )).map(n => -BigInt("0" + "1" * n, 2) -1).suspendable.foreach(value => checkBigInt(value, dut.io.s16)),
          () => Random.shuffle((0 to 31 - 1 )).map(n => -BigInt("0" + "1" * n, 2) -1).suspendable.foreach(value => checkBigInt(value, dut.io.s31)),
          () => Random.shuffle((0 to 32 - 1 )).map(n => -BigInt("0" + "1" * n, 2) -1).suspendable.foreach(value => checkBigInt(value, dut.io.s32)),
          () => Random.shuffle((0 to 62)).map(n => -BigInt("0" + "1" * n, 2) -1).suspendable.foreach(value => checkBigInt(value, dut.io.s63)),
          () => Random.shuffle((0 to 63)).map(n => -BigInt("0" + "1" * n, 2) -1).suspendable.foreach(value => checkBigInt(value, dut.io.s64)),
          () => Random.shuffle((0 to 64)).map(n => -BigInt("0" + "1" * n, 2) -1).suspendable.foreach(value => checkBigInt(value, dut.io.s65)),
          () => Random.shuffle((0 to 126)).map(n => -BigInt("0" + "1" * n, 2) -1).suspendable.foreach(value => checkBigInt(value, dut.io.s127)),
          () => Random.shuffle((0 to 127)).map(n => -BigInt("0" + "1" * n, 2) -1).suspendable.foreach(value => checkBigInt(value, dut.io.s128))
        )


        import SpinalSimVerilatorIoTest._
        def newEnumTest(test : newEnumTest) = {
          repeatSim(40){
            val e = State.elements(Random.nextInt(State.elements.length))
            test.stateInput #= e
            sleep(1)
            assert(test.stateOutput.toEnum == e)
            assert(test.stateDecoded.toInt == (1 << e.position))
          }
        }

        newEnumTest(dut.nativeEncoding)
        newEnumTest(dut.binaryOneHotEncoding)
        newEnumTest(dut.binarySequentialEncoding)
      }
    }
  }

  test("compile"){
    compiled = SimConfig.compile(new SpinalSimVerilatorIoTest.SpinalSimVerilatorIoTestTop)
  }

  test("test1") {
    doTest
  }
  test("test2") {
    doTest
  }
  test("test3") {
    doTest
  }


  test("testMulticore") {
    import scala.concurrent.ExecutionContext.Implicits.global

    val futures = for(i <- 0 to 31) yield {
      Future{
        doTest
      }
    }
    import scala.concurrent.duration._

    futures.foreach(f => Await.result(f,10 seconds))
  }



}
