package bp.context;

import bp.project.BPResourceProject;
import bp.res.BPResource;

public interface BPProjectsContext extends BPWorkspaceContext
{
	void saveProjects();

	BPResourceProject[] listProject();

	void addProject(BPResourceProject project);

	void removeProject(BPResourceProject project);

	boolean checkProjectName(String prjname);

	BPResourceProject getRootProject(BPResource res);

	BPResourceProject getOrCreateTempProject();

	void sendProjectChangedEvent();

	BPResourceProject getProjectByName(String name);

	BPResourceProject getProject(String key);

	void initProjects();
	
	void clearProjects();
}
