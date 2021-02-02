package promotion

import common.Branch
import common.Os
import common.gradleWrapper
import common.requiresOs
import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.CheckoutMode
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import promotion.vcsRoots.Gradle_Promotion__master_

// GradleBuildTool_Master_Promotion_SanityCheck
object SanityCheck : BuildType({
    // From Gradle_Promotion_MasterSanityCheck
    uuid = "bf9b573a-6e5e-4db1-88b2-399e709026b5-1"
    id = AbsoluteId("GradleBuildTool_Check_Promotion_SanityCheck")
    name = "SanityCheck"
//     GradleBuildTool_Master
//    id("SanityCheck")
    // Master
//    name = "SanityCheck"
    description = "Compilation and test execution of buildSrc"

    vcs {
        root(Gradle_Promotion__master_)

        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
        showDependenciesChanges = true
    }

    steps {
        gradleWrapper {
            tasks = "tasks"
            gradleParams = ""
            param("org.jfrog.artifactory.selectedDeployableServer.defaultModuleVersionConfiguration", "GLOBAL")
        }
    }

    triggers {
        vcs {
            branchFilter = ""
        }
    }

    requirements {
        requiresOs(Os.LINUX)
    }
})
