package co.anbora.labs.sqlfluff.ide.notifications

import com.intellij.notification.NotificationType

class LinterErrorNotification(
    content: String = ""
): LinterNotification(content, NotificationType.ERROR)