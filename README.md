# Check Java Trusted CA Certificates on Lambda

You can check installed Trusted CA Certificates in AWS Lambda. Invoke this Lambda function, and then Subject, Issuer, and PublicKey are output on CloudWatch Logs. This Lambda function is deployed using AWS SAM CLI.

__It was created for the `java8.al2` runtime, but will probably work on other Java runtimes.__

## Requirements

* SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
* Java8 - Install the Amazon Corretto 8 - [Linux](https://docs.aws.amazon.com/ja_jp/corretto/latest/corretto-8-ug/amazon-linux-install.html), [Windows](https://docs.aws.amazon.com/ja_jp/corretto/latest/corretto-8-ug/windows-7-install.html), [MacOS](https://docs.aws.amazon.com/ja_jp/corretto/latest/corretto-8-ug/macos-install.html)
* Maven - [Install Maven](https://maven.apache.org/install.html)

## Deploy

To build and deploy this project for the first time, run the following in your shell:

```bash
sam build
sam deploy --guided
```

The first command will build the source of your application. The second command will package and deploy your application to AWS, with a series of prompts:

* **Stack Name**: The name of the stack to deploy to CloudFormation. This should be unique to your account and region, and a good starting point would be something matching your project name.
* **AWS Region**: The AWS region you want to deploy your app to.
* **Confirm changes before deploy**: If set to yes, any change sets will be shown to you before execution for manual review. If set to no, the AWS SAM CLI will automatically deploy application changes.
* **Allow SAM CLI IAM role creation**: Many AWS SAM templates, including this example, create AWS IAM roles required for the AWS Lambda function(s) included to access AWS services. By default, these are scoped down to minimum required permissions. To deploy an AWS CloudFormation stack which creates or modified IAM roles, the `CAPABILITY_IAM` value for `capabilities` must be provided.
* **Save arguments to samconfig.toml**: If set to yes, your choices will be saved to a configuration file inside the project, so that in the future you can just re-run `sam deploy` without parameters to deploy changes to your application.

## Invoke Lambda function

```bash
sam remote invoke --stack-name <Your_Stack_Name>
```

## Check Lambda logs

You can check logs using CloudWatch Logs on Management Console or `sam logs` command on CLI. If you want to use shell, please execute following:

```bash
sam logs --stack-name <Your_Stack_Name>
```

A part of example output is following:

```json
[
  // :
  // :
  {
    "subject": "CN=DigiCert Global Root G2,OU=www.digicert.com,O=DigiCert Inc,C=US",
    "issuer": "CN=DigiCert Global Root G2,OU=www.digicert.com,O=DigiCert Inc,C=US",
    "publicKey": "Sun RSA public key, 2048 bits\n  params: null\n  modulus: 236...(snip)...477\n  public exponent: 65537"
  },
  // :
  // :
  {
    "subject": "L=Seattle,CN=Amazon RDS us-east-1 Root CA ECC384 G1,ST=WA,OU=Amazon RDS,O=Amazon Web Services\\, Inc.,C=US",
    "issuer": "L=Seattle,CN=Amazon RDS us-east-1 Root CA ECC384 G1,ST=WA,OU=Amazon RDS,O=Amazon Web Services\\, Inc.,C=US",
    "publicKey": "Sun EC public key, 384 bits\n  public x coord: 307...(snip)...4827\n  public y coord: 296...(snip)...944\n  parameters: secp384r1 [NIST P-384] (1.3.132.0.34)"
  },
  // :
  // :
]
```

## License

MIT