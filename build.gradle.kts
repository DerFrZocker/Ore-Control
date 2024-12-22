import org.ajoberstar.grgit.Grgit
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    jacoco
    id("org.ajoberstar.grgit") version "5.2.2"
}

val date: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

if (!project.hasProperty("gitCommitHash")) {
    apply(plugin = "org.ajoberstar.grgit")
    val gitCommitHash: String? = try {
        extensions.getByName<Grgit>("grgit").head()?.abbreviatedId
    } catch (e: Exception) {
        logger.warn("Error getting commit hash", e)
        ""
    }
    version = "$date-$gitCommitHash"
} else {
    version = date
}

group = "de.derfrzocker"
