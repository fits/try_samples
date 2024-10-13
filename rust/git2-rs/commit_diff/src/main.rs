use git2::{Delta, DiffFile, DiffFindOptions, DiffOptions, Repository};
use std::env;
use std::time::Instant;

fn main() -> Result<(), git2::Error> {
    let path = env::args().skip(1).next().unwrap_or(".".into());

    let t1 = Instant::now();

    let repo = Repository::open(path)?;

    let t2 = Instant::now();
    println!("open: {:?}", t2.duration_since(t1));

    let mut walk = repo.revwalk()?;

    walk.push_head()?;

    let t3 = Instant::now();
    println!("walk ready: {:?}", t3.duration_since(t2));

    if let Some(oid) = walk.next() {
        let commit = repo.find_commit(oid?)?;

        let t4 = Instant::now();
        println!("commit find: {:?}", t4.duration_since(t3));

        let tree = commit.tree()?;

        let old_tree = if commit.parent_count() == 1 {
            Some(commit.parent(0)?.tree()?)
        } else {
            None
        };

        let mut opts = DiffOptions::new();
        opts.minimal(true);

        let mut diff = repo.diff_tree_to_tree(old_tree.as_ref(), Some(&tree), Some(&mut opts))?;

        let t5 = Instant::now();
        println!("diff tree: {:?}", t5.duration_since(t4));

        let mut fopts = DiffFindOptions::new();
        fopts.renames(true);

        diff.find_similar(Some(&mut fopts))?;

        for d in diff.deltas() {
            match d.status() {
                Delta::Added | Delta::Deleted | Delta::Modified => {
                    if let Some(f) = path_to_str(&d.new_file()) {
                        println!("{}", f);
                    }
                }
                Delta::Renamed => {
                    println!(
                        "{}, {}",
                        path_to_str(&d.old_file()).unwrap_or(""),
                        path_to_str(&d.new_file()).unwrap_or(""),
                    );
                }
                _ => {}
            }
        }

        let t6 = Instant::now();
        println!("diff deltas: {:?}", t6.duration_since(t5));
    }

    Ok(())
}

fn path_to_str<'a>(f: &'a DiffFile) -> Option<&'a str> {
    f.path().and_then(|x| x.to_str())
}
