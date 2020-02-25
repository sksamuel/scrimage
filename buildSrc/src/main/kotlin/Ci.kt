import org.gradle.internal.os.OperatingSystem

object Ci {

   val isGithub = System.getenv("GITHUB_ACTIONS") == "true"
   val githubRunId: String = System.getenv("GITHUB_RUN_ID") ?: "0"

   val isReleaseVersion = !isGithub

   val ideaActive = System.getProperty("idea.active") == "true"
   val os: OperatingSystem = org.gradle.internal.os.OperatingSystem.current()
}
