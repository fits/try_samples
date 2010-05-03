package {
	public class UserData {

		public var id: String;
		public var name: String;

		public function UserData(item: XML = null) {
			if (item) {
				this.id = item.@id;
				this.name = item.@name;
			}
		}
	}
}