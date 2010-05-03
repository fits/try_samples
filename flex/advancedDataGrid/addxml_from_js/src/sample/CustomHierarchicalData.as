package sample
{
	import mx.collections.HierarchicalData;
	import mx.utils.ObjectUtil;

	public class CustomHierarchicalData extends HierarchicalData
	{
		public function CustomHierarchicalData(value:Object=null) {
			super(value);
		}

		public override function hasChildren(node: Object): Boolean {
			var result: Boolean = false;

			if (node != null && node is XML) {
				result = (node.@hasChild == "true");
			}

			if (!result) {
				result = super.hasChildren(node);
			}
			return result;
		}
	}
}