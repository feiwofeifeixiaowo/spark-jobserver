package spark.jobserver

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.testkit.{ ImplicitSender, TestKit }
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.sql.{ SQLContext, Row }
import org.apache.spark.sql.types._
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.scalatest.{ Matchers, FunSpecLike, FunSpec, BeforeAndAfterAll, BeforeAndAfter }
import org.apache.spark.sql.api.java._

/**
 * this Spec is a more complex version of the same one in the job-server project,
 * it uses a combination of RDDs and DataFrames instead of just RDDs
 */
class NamedObjectsSpec extends TestKit(ActorSystem("NamedObjectsSpec")) with FunSpecLike
    with ImplicitSender with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  implicit def rddPersister: NamedObjectPersister[NamedRDD[Int]] = new RDDPersister[Int]

  private var sc : SparkContext = _
  private var sqlContext : SQLContext = _
  private var namedObjects: NamedObjects = _
  
  override def beforeAll {
    sc = new SparkContext("local[3]", getClass.getSimpleName, new SparkConf)
    sqlContext = new SQLContext(sc)
    namedObjects = new JobServerNamedObjects(system)
    namedObjects.getNames.foreach { namedObjects.forget(_) }
  }
  
  def rows: RDD[Row] = sc.parallelize(List(Row(1, true), Row(2, false), Row(55, true)))

  override def afterAll() {
    sc.stop
    TestKit.shutdownActorSystem(system)
  }

  describe("NamedObjects") {
    it("get() should return None when object does not exist") {
      namedObjects.get("No such object") should equal(None)
    }

    it("get() should return Some(RDD) and Some(DF) when they exist") {
      val rdd = sc.parallelize(Seq(1, 2, 3))
      namedObjects.update("rdd1", NamedRDD(rdd, true, StorageLevel.MEMORY_ONLY))

      val NamedRDD(rdd1, _, _) = namedObjects.get[NamedRDD[Int]]("rdd1").get
      rdd1 should equal(rdd)
    }

    it("destroy() should destroy an object that exists") {
      val rdd1 = NamedRDD(sc.parallelize(Seq(1, 2, 3)), false, StorageLevel.MEMORY_ONLY)
      namedObjects.update("rdd1", rdd1)

      namedObjects.get("rdd1") should not equal None

      namedObjects.destroy(rdd1, "rdd1")
      namedObjects.get("rdd1") should equal(None)
    }

    it("getNames() should return names of all managed Objects") {
      namedObjects.getNames().size should equal(0)
      namedObjects.update("rdd1", NamedRDD(sc.parallelize(Seq(1, 2, 3)), true, StorageLevel.MEMORY_ONLY))

      namedObjects.getNames().toSeq.sorted should equal(Seq("rdd1"))
      namedObjects.forget("rdd1")
      namedObjects.getNames().size should equal(0)
    }
  }
}
