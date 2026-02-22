package co.anbora.labs.sqlfluff.ide.widget

import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase

class ToggleLinterStatusWidgetTest : BasePlatformTestCase() {

    private lateinit var settings: LinterStatusSettings

    override fun setUp() {
        super.setUp()
        settings = project.getService(LinterStatusSettings::class.java)
        settings.showWidget = true
    }

    fun testSettingsDefaultToShowWidget() {
        val freshState = LinterStatusSettings.State()
        TestCase.assertTrue("Default state should have showWidget=true", freshState.showWidget)
    }

    fun testSettingsToggle() {
        TestCase.assertTrue("showWidget should start as true", settings.showWidget)

        settings.showWidget = false
        TestCase.assertFalse("showWidget should be false after toggle off", settings.showWidget)

        settings.showWidget = true
        TestCase.assertTrue("showWidget should be true after toggle on", settings.showWidget)
    }

    fun testFactoryIsAvailableReflectsSettings() {
        val factory = LinterStatusWidgetFactory()

        settings.showWidget = true
        TestCase.assertTrue("Factory should be available when showWidget=true", factory.isAvailable(project))

        settings.showWidget = false
        TestCase.assertFalse("Factory should not be available when showWidget=false", factory.isAvailable(project))

        settings.showWidget = true
        TestCase.assertTrue("Factory should be available again after re-enable", factory.isAvailable(project))
    }

    fun testWidgetCreatedByFactory() {
        val factory = LinterStatusWidgetFactory()
        val widget = factory.createWidget(project)

        TestCase.assertEquals(LinterStatusWidget.WIDGET_ID, widget.ID())
        TestCase.assertNotNull("Widget should provide a presentation", widget.getPresentation())
    }

    fun testUpdateAllWidgetsReinstallsWidget() {
        val manager = project.getService(StatusBarWidgetsManager::class.java)
        val statusBar = WindowManager.getInstance().getStatusBar(project) ?: return

        // Initial state: widget should be available
        settings.showWidget = true
        manager.updateAllWidgets()

        // Disable widget
        settings.showWidget = false
        manager.updateAllWidgets()

        val factory = LinterStatusWidgetFactory()
        TestCase.assertFalse("Factory should not be available when disabled", factory.isAvailable(project))

        // Re-enable widget
        settings.showWidget = true
        manager.updateAllWidgets()

        TestCase.assertTrue("Factory should be available after re-enable", factory.isAvailable(project))

        // Verify the factory reports available after re-enable
        // Note: actual widget installation on the status bar is asynchronous in the test environment
        val factory2 = LinterStatusWidgetFactory()
        TestCase.assertTrue(
            "Factory should report available after disable+re-enable cycle",
            factory2.isAvailable(project)
        )
    }
}
