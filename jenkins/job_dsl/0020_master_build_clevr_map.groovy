def GIT_REPO_BRANCH_PARAM = 'sha1'

def ORGANIZATION_NAME                = 'HiVR'
def GITHUB_CLEVR_MAP_REPOSITORY      = "${ORGANIZATION_NAME}/CleVR-map"
def DEFAULT_GITHUB_REPOSITORY_BRANCH = 'master'

def HIVR_JENKINS_GITHUB_CREDENTIALS  = '5bb4e914-a7c2-4ba4-872f-35ea511b191a'

def DEFAULT_EXECUTOR = 'windows'

def masterBuild = "0020-master-build-clevr-map"

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
                github(GITHUB_CLEVR_MAP_REPOSITORY, 'https' )
            }
            branch(injectJobVariable(GIT_REPO_BRANCH_PARAM))
            shallowClone(false)
            extensions {
                cleanAfterCheckout()
                cleanBeforeCheckout()
                wipeOutWorkspace()
            }
        }
    }
    steps {
		// No API for Unity available -> configure manually using following details:
		//
		// Invoke Unity3d Editor
		//   Unity3d installation name     -> "Unity 5.3.4p1"
		//   Editor command line arguments -> "-quit -batchmode -executeMethod JenkinsCI.PerformWindowsBuild"
    }
}

def injectJobVariable(variableName) {
  return '${' + variableName + '}'
}