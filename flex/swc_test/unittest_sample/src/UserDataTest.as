package {
	import flexunit.framework.TestCase;

	public class UserDataTest extends TestCase {

		public function testSimple(): void {
			var data: UserData = new UserData(<data id="1" name="tester" />);
			
			assertEquals("1", data.id);
			assertEquals("tester", data.name);
		}
		
		public function testSimple2(): void {
			assertEquals("b", "b");
		}
	}
}