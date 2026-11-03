package edu.assignment4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Deterministic benchmark; timings are single-run observations, operation counts are the robust comparison. */
public final class ExperimentRunner {
    private static final long SEED=20260404L;
    private ExperimentRunner() {}
    public static void main(String[] args) throws Exception {
        Path out=args.length>0?Path.of(args[0]):Path.of("results.csv");
        Path plots=args.length>1?Path.of(args[1]):Path.of("plots");
        Files.createDirectories(out.toAbsolutePath().getParent()); Files.createDirectories(plots);
        List<String> rows=new ArrayList<>(); rows.add("category,algorithm,size,pattern_size,trial,answer,operations,allocated_cells,elapsed_ns,seed");
        editRows(rows);
        searchRows(rows);
        Files.write(out,rows,StandardCharsets.UTF_8);
        writePlots(out,plots);
        System.out.printf("Wrote %d measurements to %s and plots to %s%n",rows.size()-1,out.toAbsolutePath(),plots.toAbsolutePath());
    }
    private static void editRows(List<String> rows) {
        int[] sizes={2,4,6,8,10,12}; Random r=new Random(SEED);
        for(int n:sizes) for(int trial=1;trial<=3;trial++) {
            String a=randomString(r,n),b=randomString(r,n);
            if(trial==1) addEdit(rows,"recursive",n,trial,a,b,()->EditDistance.recursive(a,b));
            addEdit(rows,"full_table",n,trial,a,b,()->EditDistance.fullTable(a,b));
            addEdit(rows,"two_row",n,trial,a,b,()->EditDistance.twoRow(a,b));
        }
    }
    @FunctionalInterface private interface EditCall { EditDistance.Result run(); }
    private static void addEdit(List<String> rows,String algorithm,int n,int trial,String a,String b,EditCall call) {
        long start=System.nanoTime(); EditDistance.Result x=call.run(); long elapsed=System.nanoTime()-start;
        rows.add(String.format("edit_distance,%s,%d,%d,%d,%d,%d,%d,%d,%d",algorithm,n,n,trial,x.distance(),x.operations(),x.allocatedCells(),elapsed,SEED));
    }
    private static void searchRows(List<String> rows) {
        int[] ns={100,250,500,1000,2000,4000,8000}; Random r=new Random(SEED+1);
        for(int n:ns) for(int trial=1;trial<=5;trial++) {
            String text=randomString(r,n), pattern=randomString(r,Math.min(16,Math.max(4,n/20)));
            // Mix deterministic guaranteed hits with seeded random background; overlapping hits are possible.
            if(n>=pattern.length()*3) { StringBuilder sb=new StringBuilder(text); sb.replace(n/3,n/3+pattern.length(),pattern); sb.replace(2*n/3,2*n/3+pattern.length(),pattern); text=sb.toString(); }
            final String caseText=text, casePattern=pattern;
            addSearch(rows,"naive",n,pattern.length(),trial,caseText,casePattern,()->StringSearch.naive(caseText,casePattern));
            addSearch(rows,"kmp",n,pattern.length(),trial,caseText,casePattern,()->StringSearch.kmp(caseText,casePattern));
        }
    }
    @FunctionalInterface private interface SearchCall { StringSearch.Result run(); }
    private static void addSearch(List<String> rows,String algorithm,int n,int m,int trial,String text,String pattern,SearchCall call) {
        long start=System.nanoTime(); StringSearch.Result x=call.run(); long elapsed=System.nanoTime()-start;
        rows.add(String.format("string_search,%s,%d,%d,%d,\"%s\",%d,0,%d,%d",algorithm,n,m,trial,x.matches(),x.comparisons(),elapsed,SEED+1));
    }
    private static String randomString(Random r,int n) { StringBuilder s=new StringBuilder(n); for(int i=0;i<n;i++) s.append((char)('a'+r.nextInt(4))); return s.toString(); }
    private static void writePlots(Path csv,Path plots) throws IOException, InterruptedException {
        String python="import csv,sys,collections\nimport matplotlib; matplotlib.use('Agg')\nimport matplotlib.pyplot as plt\nfrom pathlib import Path\np=Path(sys.argv[1]); out=Path(sys.argv[2]); rows=list(csv.DictReader(p.open()))\ndef series(cat,alg):\n d=collections.defaultdict(list)\n for r in rows:\n  if r['category']==cat and r['algorithm']==alg: d[int(r['size'])].append(r)\n return sorted((n, sum(int(x['operations']) for x in v)/len(v), sum(int(x['elapsed_ns']) for x in v)/len(v), sum(int(x['allocated_cells']) for x in v)/len(v)) for n,v in d.items())\n# Edit distance: operation growth on a log axis\nfig,ax=plt.subplots(figsize=(8,5))\nfor a,label in [('recursive','Recursive'),('full_table','Full table'),('two_row','Two row')]:\n q=series('edit_distance',a); ax.plot([x[0] for x in q],[x[1] for x in q],marker='o',label=label)\nax.set_yscale('log'); ax.set(xlabel='String length (each input)',ylabel='Counted recursive calls / DP cells (log scale)',title='Edit distance: counted work vs input length'); ax.grid(True,which='both',alpha=.25); ax.legend(); fig.tight_layout(); fig.savefig(out/'edit_distance_operations_log.png',dpi=160); plt.close(fig)\n# Space comparison\nfig,ax=plt.subplots(figsize=(8,5))\nfor a,label in [('full_table','Full table'),('two_row','Two row')]:\n q=series('edit_distance',a); ax.plot([x[0] for x in q],[x[3] for x in q],marker='o',label=label)\nax.set(xlabel='String length (each input)',ylabel='Allocated integer cells',title='Edit distance: auxiliary space'); ax.grid(True,alpha=.25); ax.legend(); fig.tight_layout(); fig.savefig(out/'edit_distance_space.png',dpi=160); plt.close(fig)\n# Search comparisons and ratio\nfig,ax=plt.subplots(figsize=(8,5));\nfor a,label in [('naive','Naive'),('kmp','KMP')]:\n q=series('string_search',a); ax.plot([x[0] for x in q],[x[1] for x in q],marker='o',label=label)\nax.set(xlabel='Text length',ylabel='Character comparisons',title='String search: counted comparisons'); ax.grid(True,alpha=.25); ax.legend(); fig.tight_layout(); fig.savefig(out/'search_comparisons.png',dpi=160); plt.close(fig)\n# Comparison ratio plot (Naive / KMP)\nn=series('string_search','naive'); k=series('string_search','kmp'); d={x[0]:x[1] for x in k}; q=[(x[0],x[1]/d[x[0]] if d[x[0]] else 0) for x in n if x[0] in d]\nfig,ax=plt.subplots(figsize=(8,5)); ax.plot([x[0] for x in q],[x[1] for x in q],marker='o',color='#7b2cbf'); ax.axhline(1,color='gray',linestyle='--'); ax.set(xlabel='Text length',ylabel='Naive / KMP comparisons',title='Relative comparison count (ratio > 1 favors KMP)'); ax.grid(True,alpha=.25); fig.tight_layout(); fig.savefig(out/'search_ratio.png',dpi=160); plt.close(fig)\n";
        Path script=Files.createTempFile("daa_plots_",".py"); Files.writeString(script,python,StandardCharsets.UTF_8);
        String py=System.getenv().getOrDefault("PYTHON","python"); Process p=new ProcessBuilder(py,script.toString(),csv.toAbsolutePath().toString(),plots.toAbsolutePath().toString()).inheritIO().start(); int code=p.waitFor(); Files.deleteIfExists(script); if(code!=0) throw new IOException("Plot generation failed with exit code "+code);
    }
}
