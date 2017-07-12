import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CounterSpec : Spek({
    describe("Counter") {
        on("初期値を指定せずに作成") {
            val d = Counter()

            it("カウントは 0") {
                assertEquals(0, d.count)
            }
        }

        on("初期値を指定して作成") {
            val d = Counter(3)

            it("カウントは初期値と同じ") {
                assertEquals(3, d.count)
            }
        }

        on("0 以外の値でカウントアップ") {
            val d = Counter(1)
            val res = d.countUp(5)

            it("新しいインスタンスを返す") {
                assertTrue(d !== res)
            }

            it("カウントは初期値にカウントアップ値を加えた値") {
                assertEquals(6, res.count)
            }
        }

        on("0 でカウントアップ") {
            val d = Counter(1)
            val res = d.countUp(0)

            it("同一インスタンスを返す") {
                assertTrue(d === res)
            }
        }
    }
})
