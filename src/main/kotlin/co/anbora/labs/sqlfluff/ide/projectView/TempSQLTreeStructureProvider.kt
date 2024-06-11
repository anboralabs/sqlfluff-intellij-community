package co.anbora.labs.sqlfluff.ide.projectView

import co.anbora.labs.sqlfluff.lint.VIRTUAL_FILE_PREFIX
import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FileStatusListener
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class TempSQLTreeStructureProvider(private val project: Project) : TreeStructureProvider {

    init {
        project.messageBus
            .connect(project)
            .subscribe(VirtualFileManager.VFS_CHANGES,  object: BulkFileListener {

                override fun after(events: MutableList<out VFileEvent>) {
                    refreshProjectView()
                }
            })

        FileStatusManager.getInstance(project).addFileStatusListener(object : FileStatusListener {
            override fun fileStatusesChanged() {
                refreshProjectView()
            }
        }, project)
    }

    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): Collection<AbstractTreeNode<*>> {
        val project = parent.project ?: return children

        return when {
            parent is PsiDirectoryNode -> {
                val filtered = children.filterNot {
                    it.toTestString(null)?.startsWith(VIRTUAL_FILE_PREFIX) ?: false
                }
                filtered
            }
            else -> children
        }
    }

    private fun refreshProjectView() = ProjectView.getInstance(project).currentProjectViewPane?.updateFromRoot(true)
}
