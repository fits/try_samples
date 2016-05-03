class RelatedChangesetsController < ApplicationController
  unloadable

  def listup
    @issue = Issue.find(params[:id])
    @project = @issue.project

    @changesets = @issue.changesets.visible.preload(:repository, :user).to_a
  end

end
