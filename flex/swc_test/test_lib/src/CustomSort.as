package {
	import mx.utils.ObjectUtil;

	public class CustomSort {
		public static function sortNo(obj1: Object, obj2: Object): int {
			return ObjectUtil.numericCompare(toInt(obj1.@no), toInt(obj2.@no));
		}
		
		public static function toInt(obj: String): Number {
			var result: Number = parseInt(obj);
			return (isNaN(result))? int.MAX_VALUE: result;
		}
	}
}