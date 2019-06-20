pipeline 
{
	agent any
	stages 
	{
		stage('Checkout')
		{
			steps
			{
				gitlabCommitStatus("Checkout")
				{
				    checkout scm
				}
			}
		}
		stage('Build') 
		{
			steps 
			{
				gitlabCommitStatus('Build')
				{
				    sh 'chmod +x gradlew'
                    sh './gradlew build'

				}
			}
			
		}
		stage('Deploy')
		{
			steps
			{
				gitlabCommitStatus('Deploy')
				{
				    archiveArtifacts artifacts: 'build/libs/DefaultSettings-*.jar', fingerprint: true
                    cleanWs()

				}
			}
		}
	}
	options
	{
		gitLabConnection('Jenkins')
	}
}