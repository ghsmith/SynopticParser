# SynopticParser
Java Maven project to parse diagnosis and TNM staging from CoPath synoptic reports.

Example usage:

```
C:\stuff4>java -cp c:\users\ghsmith\Documents\NetBeansProjects\SynopticParser\target\uber-SynopticParser-1.0-SNAPSHOT.jar emory.synopticparser.Main synoptic_signature.txt synoptics201512.txt > synoptics201512.parsed.txt

Jan 10, 2016 11:55:26 PM emory.synopticparser.Main main
INFO: 3 signatures loaded
Jan 10, 2016 11:55:26 PM emory.synopticparser.Main main
INFO: 206 reports loaded

C:\stuff4>type synoptics201512.parsed.txt

S15-xxxxx       lung    Typical carcinoid tumor pT1aN0
S15-xxxxx       breast  Invasive ductal carcinoma (no special type or not otherwise specified)  pT1cN1mi
S15-xxxxx       breast  Invasive ductal carcinoma (no special type or not otherwise specified)  pT1cN1mi
S15-xxxxx       breast  Invasive ductal carcinoma (no special type or not otherwise specified)  pT1cN1mi
S15-xxxxx       breast  No residual invasive carcinoma after presurgical (neoadjuvant) therapy  pTisN0
S15-xxxxx       pancreas        Adenocarcinoma (not otherwise characterized) pT2N0
S15-xxxxx       lung    Squamous cell carcinoma pT1bN0
.
.
.
```
