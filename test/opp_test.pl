#
# opp_test.pl: simple testing tool for OMNeT++ regression tests
#

#
# TODO/FIXME:
#  - more powerful tests, not only %contains and %not-contains
#  - %contains --> %check-output:
#

#
# If no args, print usage
#
$Usage = 'OMNeT++ Regression Test Tool, 2002
Syntax: opp_test [-g|-r] [-v] [-d] [-w <dir>] <testfile> ...
  -g         generate (export) source files from test case files
  -r         run test (expects pre-built test executable)
  -v         verbose
  -d         very verbose (debug)
  -w <dir>   working directory (defaults to `work\')
  -s <prog>  shell to use to run test program
  -p <prog>  name of test program (defaults to name of working directory)

Usage in nutshell:
   1. create *.test files
   2. run opp_test with -g option to generate the source files from *.test
   3. create a makefile (opp_makemake) and build the test program
   4. run opp_test with -r option to execute the tests

All files will be created in the working directory.

';

# .test file possible entries. legend: 1=once, v=has value, b=has body, f=value is filename
%Entries = (
    'description'        => '1b',

    'activity'           => '1b',
    'module'             => '1vb',
    'module_a'           => '1vb',
    'module_b'           => '1vb',

    'file'               => 'vbf',
    'inifile'            => '1vbf',

    'contains'           => 'vbf',
    'not-contains'       => 'vbf',
    'exitcode'           => '1v',
    'ignore-exitcode'    => '1v'
);

#
#  Parse the command line for options and files.
#
@filenames = ();
$mode='';
$workingdir = 'work';
$shell='/bin/sh';
$testprogram='';
$verbose=0;
$debug=0;

$arg0 = "opp_test";

if ($#ARGV == -1)
{
    print $Usage;
    exit(1);
}

while (@ARGV)
{
    $arg = shift @ARGV;

    if ($arg eq "-v") {
        $verbose=1;
    }
    elsif ($arg eq "-d") {
        $debug=1;
    }
    elsif ($arg eq "-w") {
        $workingdir = shift @ARGV;
    }
    elsif ($arg eq "-s") {
        $shell = shift @ARGV;
    }
    elsif ($arg eq "-p") {
        $testprogram = shift @ARGV;
    }
    elsif ($arg eq "-g") {
        $mode='gen';
    }
    elsif ($arg eq "--generate-files") {
        $mode='gen';
    }
    elsif ($arg eq "-r") {
        $mode='run';
    }
    elsif ($arg eq "--run") {
        $mode='run';
    }
    elsif ($arg =~ /^-/) {
        print $Usage;
        exit(1);
    }
    else {
        push(@filenames,$arg);
    }
}

if ($mode eq '')
{
    print $Usage;
    exit(1);
}

# test existence of working directory
if (! -d $workingdir) {
    print stderr "$arg0: error: working directory `$workingdir' does not exist\n";
    exit(1);
}

# produce name of test program
if ($testprogram eq '') {

    if ($ENV{'OS'} =~ /windows/i) {
      $exesuffix='.exe';
    } else {
      $exesuffix='';
    }

    $workingdir =~ /([^\/\\]*)$/;
    $testprogram = $1.$exesuffix;
}

#
# generate test files
#
if ($mode eq 'gen')
{
    define_templates();

    foreach $testfilename (@filenames)
    {
        testcase_generatesources($testfilename);
    }
}

#
# run tests
#
if ($mode eq 'run')
{
    $num_pass=0;
    $num_fail=0;
    $num_unresolved=0;

    foreach $testfilename (@filenames)
    {
        testcase_run($testfilename);
    }

    print "========================================\n";
    print "PASS: $num_pass   FAIL: $num_fail   UNRESOLVED: $num_unresolved\n";
}

sub parse_testfile
{
    my $testfilename = shift;

    undef %bodies;
    undef %values;
    undef %count;

    print "  parsing $testfilename\n" if ($debug);

    # assign a test name (filename without extension, special chars removed)
    $testfilename =~ /([^\/\\]*)$/;
    $testname = $1;
    $testname =~ s/\.[^.]*$//;
    $testname =~ s/[^A-Za-z0-9_]/_/g;
    print "  testname for `$testfilename' is $testname\n" if ($debug);

    # read test file
    if (!open(IN,$testfilename)) {
        print stderr "$arg0: error: cannot open test file `$testfilename'\n"; exit(1);
    }

    $body='';
    $key_index='';
    while (<IN>)
    {
        s/[\r\n]*$//;
        if (/^%#/) {
            # ignore
        } elsif (/^%/) {
            $bodies{$key_index}=$body;
            $body='';

            /^%([^:]*):?(.*?)$/;
            $key = $1;
            $value =$2;
            $key =~ s/^\s*(.*?)\s*$/\1/;
            $value =~ s/^\s*(.*?)\s*$/\1/;

            $count{$key}++;
            $key_index = $key.'('.$count{$key}.')';
            $values{$key_index}=$value;
        } else {
            $body.= $_."\n";
        }
    }
    $bodies{$key_index}=$body;
    close(IN);

    # check entries
    foreach $key_index (keys(%values))
    {
        $key_index =~ /(.*)\((.*)\)/;
        $key = $1;
        $index = $2;
        $desc = $Entries{$key};
        if ($desc eq '') {
            print stderr "$arg0: error in test file `$testfilename': invalid entry `%$key'\n"; exit(1);
        }
        if ($desc =~ /1/ && $index>1) {
            print stderr "$arg0: error in test file `$testfilename': entry `%$key' should occur only once.\n"; exit(1);
        }
        if ($desc =~ /v/ && $values{$key_index} =~ /^\s*$/) {
            print stderr "$arg0: error in test file `$testfilename': entry `%$key' expects value after ':'\n"; exit(1);
        }
        if (!$desc =~ /v/ && !$values{$key_index} =~ /\s*/) {
            print stderr "$arg0: error in test file `$testfilename': entry `%$key' expects nothing after ':'\n"; exit(1);
        }
        if (!$desc =~ /b/ && !$bodies{$key_index} =~ /\s*/) {
            print stderr "$arg0: error in test file `$testfilename': entry `%$key' expects no body\n"; exit(1);
        }
    }

    # additional manual tests
    if (defined($bodies{'activity(1)'}) && (defined($bodies{'module(1)'}) ||
        defined($bodies{'module_a(1)'}) || defined($bodies{'module_b(1)'})))
    {
        print stderr "$arg0: error in test file `$testfilename': %activity excludes %module... entries\n"; exit(1);
    }
    if (defined($bodies{'module(1)'}) &&
        (defined($bodies{'module_a(1)'}) || defined($bodies{'module_b(1)'})))
    {
        print stderr "$arg0: error in test file `$testfilename': %module excludes %module_[a|b] entries\n"; exit(1);
    }
    if (defined($bodies{'module_a(1)'}) && !defined($bodies{'module_b(1)'}))
    {
        print stderr "$arg0: error in test file `$testfilename': %module_a without %module_b\n"; exit(1);
    }
    if (defined($bodies{'module_b(1)'}) && !defined($bodies{'module_a(1)'}))
    {
        print stderr "$arg0: error in test file `$testfilename': %module_b without %module_a\n"; exit(1);
    }

    # substitute TESTNAME and other macros, kill comments
    foreach $key (keys(%values))
    {
        $bodies{$key} =~ s/^%#.*?$//mg;

        $values{$key} =~ s/\@TESTNAME\@/$testname/g;
        $bodies{$key} =~ s/\@TESTNAME\@/$testname/g;

        $values{$key} =~ s/{([a-zA-Z0-9_]+)}/${testname}_\1/g;
        $bodies{$key} =~ s/{([a-zA-Z0-9_]+)}/${testname}_\1/g;

        $values{$key} =~ s/{}/${testname}/g;
        $bodies{$key} =~ s/{}/${testname}/g;
    }
}

sub testcase_generatesources
{
    my $testfilename = shift;

    parse_testfile($testfilename);

    print "  generating files for `$testfilename':\n" if ($debug);

    # source files
    foreach $key (keys(%values))
    {
        if ($key =~ /^file\b/)
        {
            # write out file
            $outfname = $workingdir."/".$values{$key};
            writefile($outfname, $bodies{$key});
        }
    }

    # 'activity' template
    if (defined($bodies{'activity(1)'}))
    {
        $activity = $bodies{'activity(1)'};

        # generate NED
        $ned = $ModuleNEDTemplate;
        $ned =~ s/\@TESTNAME\@/$testname/g;
        $ned =~ s/\@MODULE\@/$testname/g;
        $ned =~ s/{([a-zA-Z0-9_]+)}/${testname}_\1/g;
        $nedfname = $workingdir."/".$testname.'.ned';
        writefile($nedfname, $ned);

        # generate C++
        $cpp = $ActivityCPPTemplate;
        $cpp =~ s/\@TESTNAME\@/$testname/g;
        $cpp =~ s/\@ACTIVITY\@/$activity/g;
        $cpp =~ s/{([a-zA-Z0-9_]+)}/${testname}_\1/g;
        $cppfname = $workingdir."/".$testname.'.cc';
        writefile($cppfname, $cpp);
    }

    # 'module' template
    if (defined($bodies{'module(1)'}))
    {
        $module = $values{'module(1)'};
        $module_src = $bodies{'module(1)'};

        # generate NED
        $ned = $ModuleNEDTemplate;
        $ned =~ s/\@TESTNAME\@/$testname/g;
        $ned =~ s/\@MODULE\@/$module/g;
        $nedfname = $workingdir."/".$testname.'.ned';
        writefile($nedfname, $ned);

        # generate C++
        $cpp = $ModuleCPPTemplate;
        $cpp =~ s/\@MODULE\@/$module/g;
        $cpp =~ s/\@MODULE_SRC\@/$module_src/g;
        $cppfname = $workingdir."/".$testname.'.cc';
        writefile($cppfname, $cpp);
    }

    # 'module_a' + 'module_b' template
    if (defined($bodies{'module_a(1)'}))
    {
        $module_a = $values{'module_a(1)'};
        $module_b = $values{'module_b(1)'};
        $module_a_src = $bodies{'module_a(1)'};
        $module_b_src = $bodies{'module_b(1)'};

        # generate NED
        $ned = $ModuleABNEDTemplate;
        $ned =~ s/\@TESTNAME\@/$testname/g;
        $ned =~ s/\@MODULE_A\@/$module_a/g;
        $ned =~ s/\@MODULE_B\@/$module_b/g;
        $nedfname = $workingdir."/".$testname.'.ned';
        writefile($nedfname, $ned);

        # generate C++
        $cpp = $ModuleABCPPTemplate;
        $cpp =~ s/\@TESTNAME\@/$testname/g;
        $cpp =~ s/\@MODULE_A\@/$module_a/g;
        $cpp =~ s/\@MODULE_B\@/$module_b/g;
        $cpp =~ s/\@MODULE_A_SRC\@/$module_a_src/g;
        $cpp =~ s/\@MODULE_B_SRC\@/$module_b_src/g;
        $cppfname = $workingdir."/".$testname.'.cc';
        writefile($cppfname, $cpp);
    }

    # ini file
    $inifname = $values{'inifile(1)'};
    if ($inifname eq '') {
        $inifname = $testname.'.ini';
    }
    $inifname = $workingdir."/".$inifname;

    $inifile = $bodies{'inifile(1)'};
    if ($inifile =~ /^\s*$/s)
    {
        $inifile = $INITemplate;
        $inifile =~ s/\@TESTNAME\@/$testname/g;
        $inifile =~ s/{([a-zA-Z0-9_]+)}/${testname}_\1/g;
    }
    writefile($inifname, $inifile);
}

sub testcase_run()
{
    my $testfilename = shift;

    parse_testfile($testfilename);

    $outfname = $workingdir.'/'.$testname.'.out';
    $errfname = $workingdir.'/'.$testname.'.err';

    # delete temp files before running the test case
    foreach $key (keys(%values))
    {
        if ($key =~ /^contains\b/ || $key =~ /^not-contains\b/)
        {
            # read file
            if ($values{$key} eq 'stdout') {
                $infname = $outfname;
            }
            elsif ($values{$key} eq 'stderr') {
                $infname = $errfname;
            }
            else {
                $infname = $values{$key};
            }

            if (-f $infname) {
                print "  deleting old copy of file `$infname'\n" if ($debug);
                unlink $workingdir.'/'.$infname;
            }
        }
    }

    # ini file
    $inifname = $values{'inifile(1)'};
    if ($inifname eq '') {
        $inifname = $testname.'.ini';
    }

    # run the program
    print "  running: $shell -c 'cd $workingdir && $testprogram -f $inifname' >$outfname 2>$errfname\n" if ($debug);
    $status = system ("$shell -c 'cd $workingdir && $testprogram -f $inifname' >$outfname 2>$errfname");
    if ($status ne '0') {
        if (256*int($status/256) != $status) {
            unresolved($testfilename, "could not execute test program, exit code: $status");
            return;
        } else {
            $status = $status/256;
            if ($values{'ignore-exitcode(1)'}) {
                print "  ignoring exitcode\n" if ($debug);
            } elsif ($status == $values{'exitcode(1)'}) {
                print "  exitcode ok ($status)\n" if ($debug);
            } elsif ($values{'exitcode(1)'} ne '') {
                fail($testfilename, "test program returned exit code $status instead of $values{'exitcode(1)'}");
                return;
            } else {
                fail($testfilename, "test program returned nonzero exit code: $status");
                return;
            }
        }
    }

    # check output files
    foreach $key (keys(%values))
    {
        if ($key =~ /^contains\b/ || $key =~ /^not-contains\b/)
        {
            # read file
            if ($values{$key} eq 'stdout') {
                $infname = $outfname;
            }
            elsif ($values{$key} eq 'stderr') {
                $infname = $errfname;
            }
            else {
                $infname = $workingdir."/".$values{$key};
            }

            print "  checking $infname\n" if ($debug);

            if (!open(IN,"$infname")) {
                unresolved($testfilename, "cannot read test case output file `$infname'");
                return;
            }
            $txt = '';
            while (<IN>)
            {
                s/[\r\n]*$//;
                $txt.= $_."\n";
            }
            close IN;

            # get pattern
            $pattern = $bodies{$key};
            $pattern =~ s/^\s*(.*?)\s*$/\1/s; # trim pattern

            # check contains or not-contains
            if ($key =~ /^contains\b/) {
                if (!($txt =~ /$pattern/s)) {
                   fail($testfilename, "$values{$key} fails %contains rule");
                   if (length($txt)<=8192) {
                      print "expected pattern:\n>>>>$pattern<<<<\nactual output:\n>>>>$txt<<<<\n" if ($verbose);
                   } else {
                      print "expected pattern:\n>>>>$pattern<<<<\nactual output too big to dump (>8K), see file in working directory\n" if ($verbose);
                   }
                   return;
                }
            }
            if ($key =~ /^not-contains\b/) {
                if ($txt =~ /$pattern/s) {
                   fail($testfilename, "$values{$key} fails %not-contains rule");
                   if (length($txt)<=8192) {
                      print "expected pattern:\n>>>>$pattern<<<<\nactual output:\n>>>>$txt<<<<\n" if ($verbose);
                   } else {
                      print "expected pattern:\n>>>>$pattern<<<<\nactual output too big to dump (>8K), see file in working directory\n" if ($verbose);
                   }
                   return;
                }
            }
        }
    }
    pass($testfilename);
}

sub unresolved()
{
    my $testname = shift;
    my $reason = shift;
    $num_unresolved++;
    $result{$testname} = 'UNRESOLVED';
    $reason{$testname} = $reason;
    print "*** $testname: UNRESOLVED ($reason)\n";
}

sub fail()
{
    my $testname = shift;
    my $reason = shift;
    $num_fail++;
    $result{$testname} = 'FAIL';
    $reason{$testname} = $reason;
    print "*** $testname: FAIL ($reason)\n";
}

sub pass()
{
    my $testname = shift;
    $num_pass++;
    $result{$testname} = 'PASS';
    $reason{$testname} = '';
    print "*** $testname: PASS\n";
}

sub writefile()
{
    my $fname = shift;
    my $content = shift;

    # write file but preserve file date if it already existed with identical contents
    # (to speed up make process)

    my $skipwrite = 0;
    if (-r $fname) {
        if (!open(IN,$fname)) {
            print stderr "$arg0: error: cannot read file `$fname'\n";
            exit(1);
        }
        my $oldcontent = '';
        while (<IN>)
        {
            s/[\r\n]*$//;
            $oldcontent.= $_."\n";
        }
        close(IN);

        if ($content eq $oldcontent) {
            $skipwrite = 1;
        }
    }

    if ($skipwrite) {
        print "  file `$fname' already exists with identical content\n" if ($debug);
    } else {
        print "  writing `$fname'\n" if ($debug);
        if (!open(OUT,">$fname")) {
            print stderr "$arg0: error: cannot write file `$fname'\n";
            exit(1);
        }
        print OUT $content;
        close OUT;
    }
}

sub define_templates()
{
    $ModuleNEDTemplate = '
simple @MODULE@
endsimple

network @TESTNAME@_network : @MODULE@
endnetwork
';

    $ModuleABNEDTemplate = '
simple @MODULE_A@
    gates:
        in: in;
        out: out;
endsimple

simple @MODULE_B@
    gates:
        in: in;
        out: out;
endsimple

module @TESTNAME@
    submodules:
        the@MODULE_A@ : @MODULE_A@;
        the@MODULE_B@ : @MODULE_B@;
    connections:
        the@MODULE_A@.out --> the@MODULE_B@.in;
        the@MODULE_A@.in  <-- the@MODULE_B@.out;
endmodule

network @TESTNAME@_network : @TESTNAME@
endnetwork
';

    $ActivityCPPTemplate = '
#include "omnetpp.h"

class @TESTNAME@ : public cSimpleModule
{
    Module_Class_Members(@TESTNAME@,cSimpleModule,16384)
    virtual void activity();
};

Define_Module(@TESTNAME@);

void @TESTNAME@::activity()
{
@ACTIVITY@
}
';

    $ModuleCPPTemplate = '
#include "omnetpp.h"

@MODULE_SRC@
';

    $ModuleABCPPTemplate = '
#include "omnetpp.h"

@MODULE_A_SRC@

@MODULE_B_SRC@
';

    $INITemplate = '
[General]
network=@TESTNAME@_network
output-vector-file = @TESTNAME@.vec
output-scalar-file = @TESTNAME@.sca
';
}

