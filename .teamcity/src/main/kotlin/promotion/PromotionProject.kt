package promotion

import common.Branch
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.VersionedSettings
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.versionedSettings

class PromotionProject(branch: Branch) : Project({
    uuid = "16c9f3e3-36a9-4596-a35c-70a3c7a2c5c8-1"
    id("Promotion")
    name = "Promotion"

//    val nightlyMasterSnapshot = PublishNightlySnapshot(uuid = "01432c63-861f-4d08-ae0a-7d127f63096e", branch = "master", hour = 0)
//    val masterSnapshotFromQuickFeedback = PublishNightlySnapshotFromQuickFeedback(uuid = "9a55bec1-4e70-449b-8f45-400093505afb", branch = "master")
//    val nightlyReleaseSnapshot = PublishNightlySnapshot(uuid = "1f5ca7f8-b0f5-41f9-9ba7-6d518b2822f0", branch = "release", hour = 1)
//    val releaseSnapshotFromQuickFeedback = PublishNightlySnapshotFromQuickFeedback(uuid = "eeff4410-1e7d-4db6-b7b8-34c1f2754477", branch = "release")

    buildType(SanityCheck)
//    buildType(PublishNightlySnapshot)
//    buildType(PublishBranchSnapshotFromQuickFeedback)
//    buildType(StartReleaseCycle)
//    buildType(PublishMilestone)
//    buildType(PublishReleaseCandidate)
//    buildType(PublishFinalRelease)
//    buildType(StartReleaseCycleTest)


    params {
        password("env.ORG_GRADLE_PROJECT_gradleS3SecretKey", "credentialsJSON:1d713842-74ae-48ef-8a89-c60fc1704545")
        password("env.ORG_GRADLE_PROJECT_artifactoryUserPassword", "credentialsJSON:2b7529cd-77cd-49f4-9416-9461f6ac9018")
        param("env.ORG_GRADLE_PROJECT_gradleS3AccessKey", "AKIAQBZWBNAJCJGCAMFL")
        password("env.DOTCOM_DEV_DOCS_AWS_SECRET_KEY", "credentialsJSON:853fec36-91c4-4815-9a04-c9073b497352")
        param("env.DOTCOM_DEV_DOCS_AWS_ACCESS_KEY", "AKIAX5VJCER2X7DPYFXF")
        password("env.ORG_GRADLE_PROJECT_sdkmanToken", "credentialsJSON:64e60515-68db-4bbd-aeae-ba2e058ac3cb")
        param("env.JAVA_HOME", "%linux.java11.openjdk.64bit%")
        param("env.ORG_GRADLE_PROJECT_artifactoryUserName", "bot-build-tool")
        password("env.ORG_GRADLE_PROJECT_infrastructureEmailPwd", "credentialsJSON:ea637ef1-7607-40a4-be39-ef1aa8bc5af0")
        param("env.ORG_GRADLE_PROJECT_sdkmanKey", "8ed1a771bc236c287ad93c699bfdd2d7")
    }

    buildTypesOrder = arrayListOf(
        SanityCheck
    )
})
