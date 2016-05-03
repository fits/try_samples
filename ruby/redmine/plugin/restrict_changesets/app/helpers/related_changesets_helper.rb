module RelatedChangesetsHelper

  def link_to_revision(changeset)

    rev = changeset.identifier
    repository = changeset.repository

    title = "#{l(:label_revision)} #{changeset.format_identifier}"

    link_to(
      h(title),
      {:controller => 'repositories', :action => 'revision', :id => repository.project, :repository_id => repository.identifier_param, :rev => rev},
      :title => title
    )
  end

end
