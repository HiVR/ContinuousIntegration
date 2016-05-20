def GIT_REPO_BRANCH_PARAM = 'sha1'

def ORGANIZATION_NAME                = 'HiVR'
def GITHUB_TRACKING_REPOSITORY       = "${ORGANIZATION_NAME}/HiVR"
def DEFAULT_GITHUB_REPOSITORY_BRANCH = 'master'

def HIVR_JENKINS_GITHUB_CREDENTIALS  = '5bb4e914-a7c2-4ba4-872f-35ea511b191a'

def DEFAULT_EXECUTOR = 'linux'

def trackingRepoUpdate = "0000-tracking-repo-update"

freeStyleJob(trackingRepoUpdate) {
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
        sshAgent(HIVR_JENKINS_GITHUB_CREDENTIALS)
    }
    triggers {
        cron('H/15 * * * *')
    }
    scm {
        git {
            remote {
                github(GITHUB_TRACKING_REPOSITORY, 'ssh' )
                credentials(HIVR_JENKINS_GITHUB_CREDENTIALS)
            }
            branch(DEFAULT_GITHUB_REPOSITORY_BRANCH)
            shallowClone(false)
            extensions {
                cleanAfterCheckout()
                cleanBeforeCheckout()
                wipeOutWorkspace()
            }
            recursiveSubmodules(true)
            trackingSubmodules(true)
        }
    }
    steps {
        shell(makeMultiline([
                'git config --global user.email "jenkins@hivr.nl"',
                'git config --global user.name "hivr-jenkins"',
                'if [ -z "$(git status -su)" ]; then',
                '  echo "==> No submodule changed"',
                'else',
                '  echo "==> Updating all submodules in remote repository"',
                '  git add --all',
                '  git commit -m "Update all submodules to latest HEAD"',
                '  git push origin HEAD:master',
                'fi'
        ]))
    }
}

def makeMultiline(lines) {
    return listToStringWithSeparator('\n', lines)
}

def listToStringWithSeparator(separator, list) {
    return list.join(separator)
}