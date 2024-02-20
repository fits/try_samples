use csv::Reader;
use tch::Tensor;

#[derive(Debug)]
pub struct Dataset {
    pub data: Tensor,
    pub labels: Tensor,
    pub size: i64,
}

impl Dataset {
    pub fn load(path: &str) -> Result<Self, Box<dyn std::error::Error>> {
        let mut reader = Reader::from_path(path)?;

        let mut data: Vec<f32> = vec![];
        let mut labels: Vec<u8> = vec![];

        for r in reader.records() {
            let rec = r?;

            data.extend([
                to_f32(rec.get(0))?,
                to_f32(rec.get(1))?,
                to_f32(rec.get(2))?,
                to_f32(rec.get(3))?,
            ]);

            let kind = match rec.get(4) {
                Some("setosa") => 0,
                Some("versicolor") => 1,
                _ => 2,
            };

            labels.extend([kind]);
        }

        Ok(Self {
            data: Tensor::from_slice(&data).view((-1, 4)),
            labels: Tensor::from_slice(&labels),
            size: labels.len() as i64,
        })
    }
}

fn to_f32(s: Option<&str>) -> Result<f32, String> {
    if let Some(s) = s {
        s.parse().map_err(|e| format!("{}", e))
    } else {
        Err("none".into())
    }
}
