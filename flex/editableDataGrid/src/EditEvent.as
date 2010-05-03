package
{
	import flash.events.Event;
	
	public class EditEvent extends Event
	{
		private var _rowIndex: int;
		private var _value: String;
		
		public function EditEvent(type: String, editRowIndex: int, editValue: String)
		{
			super(type);
			this._rowIndex = editRowIndex;
			this._value = editValue;
		}

		public function get rowIndex(): int {
			return this._rowIndex;
		}

		public function get value(): String {
			return this._value
		}
	}
}