def GIT_REPO_BRANCH_PARAM = 'sha1'

def ORGANIZATION_NAME                = 'HiVR'
def GITHUB_HIVRCLIENT_REPOSITORY     = "${ORGANIZATION_NAME}/HiVRClient"
def DEFAULT_GITHUB_REPOSITORY_BRANCH = 'master'

def HIVR_JENKINS_GITHUB_CREDENTIALS  = '5bb4e914-a7c2-4ba4-872f-35ea511b191a'

def DEFAULT_EXECUTOR = 'windows'

def masterBuild = "0010-master-build"

freeStyleJob(masterBuild) {
    parameters {
        stringParam(GIT_REPO_BRANCH_PARAM, 'master', 'Branch to be built')
    }
    label(DEFAULT_EXECUTOR)
    concurrentBuild()
    throttleConcurrentBuilds {
        maxPerNode(1)
    }
    logRotator {
        numToKeep(50)
        artifactNumToKeep(10)
    }
    wrappers {
        colorizeOutput('xterm')
        timestamps()
    }
    triggers {
        githubPush()
    }
    scm {
        git {
            remote {
                github(GITHUB_HIVRCLIENT_REPOSITORY, 'https' )
            }
            branch(DEFAULT_GITHUB_REPOSITORY_BRANCH)
            shallowClone(false)
            extensions {
                cleanAfterCheckout()
                cleanBeforeCheckout()
                wipeOutWorkspace()
            }
        }
    }
    steps {
		batchFile("C:\\Apps\\nuget.exe restore \"C:\\Jenkins\\workspace\\0010-master-build\\HiVRClient.sln\"")
        msBuild {
            msBuildInstallation('Microsoft Build Tools 2015')
            buildFile('HiVRClient.sln')
			args('/p:Configuration=Release')
        }
    }
	publishers {
        warnings(['MSBuild'], [:])
    }
}
