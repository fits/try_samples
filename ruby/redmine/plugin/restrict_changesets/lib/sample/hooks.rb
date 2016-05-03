module Sample
	class Hooks < Redmine::Hook::ViewListener

		render_on :view_issues_sidebar_queries_bottom,
			:partial => 'hooks/sample/view_issues_sidebar_queries_bottom'
	end
end
